package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
import com.wolfhack.vetoptim.common.dto.pet.PetDTO;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.client.PetClient;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceUsedMapper;
import com.wolfhack.vetoptim.taskresource.mapper.TaskMapper;
import com.wolfhack.vetoptim.taskresource.model.Staff;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ResourceUsedMapper resourceUsageMapper;
    private final PetClient petClient;
    private final BillingClient billingClient;

    private final TaskEventPublisher taskEventPublisher;
    private final ResourceAllocationService resourceAllocationService;
    private final TaskHistoryService taskHistoryService;
    private final WorkloadBalancingService workloadBalancingService;
    private final TaskAssignmentService taskAssignmentService;

    @Override
    public List<TaskDTO> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll().stream()
            .map(taskMapper::toDTO)
            .toList();
    }

    @Override
    @Cacheable("tasks")
    public Optional<TaskDTO> getTaskById(Long id) {
        log.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id).map(taskMapper::toDTO);
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Creating task for pet ID: {}", taskDTO.getPetId());
        PetDTO pet = petClient.getPetById(taskDTO.getPetId());

        Task task = taskMapper.toModel(taskDTO);

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);
        assignedStaff.ifPresent(staff -> {
            log.info("Assigned task to staff: {}", staff.getName());
            task.setAssignedStaff(staff);
        });

        resourceAllocationService.allocateResourcesForTask(task);
        workloadBalancingService.balanceWorkloadAndAssignTask(task);

        TaskCreatedEvent event = new TaskCreatedEvent(
            task.getId(),
            task.getPetId(),
            task.getTaskType().toString(),
            task.getDescription()
        );

        taskEventPublisher.publishTaskCreatedEvent(event);

        taskHistoryService.logTaskChange(task, "Task created");

        Task savedTask = taskRepository.save(task);
        log.debug("Task created with ID: {}", savedTask.getId());

        return taskMapper.toDTO(savedTask);
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with ID: {}", id);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        taskMapper.updateTaskFromDTO(taskDTO, task);

        if (taskDTO.getStatus() == TaskStatus.COMPLETED) {
            TaskCompletedEvent event = new TaskCompletedEvent(
                task.getId(),
                task.getPetId(),
                task.getTaskType().name(),
                task.getDescription(),
                task.getStatus()
            );
            taskEventPublisher.publishTaskCompletedEvent(event);
        }

        taskHistoryService.logTaskChange(task, "Task updated");

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated with ID: {}", updatedTask.getId());

        return taskMapper.toDTO(updatedTask);
    }

    @Override
    public TaskDTO completeTask(Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setStatus(TaskStatus.COMPLETED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} completed for pet ID: {}", id, task.getPetId());

                billingClient.sendTaskBillingRequest(
                    new TaskBillingRequest(
                        task.getId(),
                        task.getPetId(),
                        task.getDescription(),
                        task.getTaskType(),
                        resourceUsageMapper.toDTO(savedTask.getResourcesUsed())
                    )
                );

                TaskCompletedEvent event = new TaskCompletedEvent(
                    task.getId(),
                    task.getPetId(),
                    task.getTaskType().name(),
                    task.getDescription(),
                    TaskStatus.COMPLETED
                );

                taskEventPublisher.publishTaskCompletedEvent(event);

                return taskMapper.toDTO(savedTask);
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public TaskDTO failTask(Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setStatus(TaskStatus.FAILED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} failed for pet ID: {}", id, task.getPetId());

                TaskCompletedEvent event = new TaskCompletedEvent(
                    task.getId(),
                    task.getPetId(),
                    task.getTaskType().name(),
                    task.getDescription(),
                    TaskStatus.FAILED
                );

                taskEventPublisher.publishTaskCompletedEvent(event);

                return taskMapper.toDTO(savedTask);
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public TaskDTO escalateTask(Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setStatus(TaskStatus.ESCALATED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} escalated for pet ID: {}", id, task.getPetId());

                TaskCompletedEvent event = new TaskCompletedEvent(
                    task.getId(),
                    task.getPetId(),
                    task.getTaskType().name(),
                    task.getDescription(),
                    TaskStatus.ESCALATED
                );

                taskEventPublisher.publishTaskCompletedEvent(event);

                return taskMapper.toDTO(savedTask);
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
        log.info("Task deleted with ID: {}", id);
    }
}
