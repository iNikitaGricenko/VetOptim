package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.dto.PetDTO;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.common.dto.billing.TaskBillingRequest;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

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

    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll();
    }

    @Cacheable("tasks")
    public Optional<Task> getTaskById(Long id) {
        log.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        log.info("Creating task for pet ID: {}", task.getPetId());
        PetDTO pet = petClient.getPetById(task.getPetId());

        Optional<Staff> assignedStaff = taskAssignmentService.assignTaskToStaff(task);
        assignedStaff.ifPresent(staff -> {
            log.info("Assigned task to staff: {}", staff.getName());
            task.setAssignedStaff(staff);
        });

        resourceAllocationService.allocateResourcesForTask(task);
        workloadBalancingService.balanceWorkloadAndAssignTask(task);

        TaskCreatedEvent event = new TaskCreatedEvent(task.getId(), task.getPetId(), task.getTaskType().toString(), task.getDescription());
        taskEventPublisher.publishTaskCreatedEvent(event);

        taskHistoryService.logTaskChange(task, "Task created");

        Task savedTask = taskRepository.save(task);
        log.debug("Task created with ID: {}", savedTask.getId());

        return savedTask;
    }

    public Task updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with ID: {}", id);
        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.error("Task not found with ID: {}", id);
            return new RuntimeException("Task not found");
        });
        taskMapper.updateTaskFromDTO(taskDTO, task);

        if (taskDTO.getStatus() == TaskStatus.COMPLETED) {
            TaskCompletedEvent event = new TaskCompletedEvent(task.getId(), task.getPetId(), task.getTaskType().name(), task.getDescription(), task.getStatus());
            taskEventPublisher.publishTaskCompletedEvent(event);
        }

        taskHistoryService.logTaskChange(task, "Task updated");

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated with ID: {}", updatedTask.getId());

        return updatedTask;
    }

        public Task completeTask(Long taskId) {
        return taskRepository.findById(taskId)
            .map(task -> {
                task.setStatus(TaskStatus.COMPLETED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} completed for pet ID: {}", taskId, task.getPetId());

                billingClient.sendTaskBillingRequest(new TaskBillingRequest(
                    task.getId(),
                    task.getPetId(),
                    task.getDescription(),
                    task.getTaskType(),
                    resourceUsageMapper.toDTO(savedTask.getResourcesUsed())
                ));

                TaskCompletedEvent event = new TaskCompletedEvent(task.getId(), task.getPetId(), task.getTaskType().name(), task.getDescription(), TaskStatus.COMPLETED);
                taskEventPublisher.publishTaskCompletedEvent(event);

                return savedTask;
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task failTask(Long taskId) {
        return taskRepository.findById(taskId)
            .map(task -> {
                task.setStatus(TaskStatus.FAILED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} failed for pet ID: {}", taskId, task.getPetId());

                TaskCompletedEvent event = new TaskCompletedEvent(task.getId(), task.getPetId(), task.getTaskType().name(), task.getDescription(), TaskStatus.FAILED);
                taskEventPublisher.publishTaskCompletedEvent(event);

                return savedTask;
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task escalateTask(Long taskId) {
        return taskRepository.findById(taskId)
            .map(task -> {
                task.setStatus(TaskStatus.ESCALATED);
                Task savedTask = taskRepository.save(task);
                log.info("Task {} escalated for pet ID: {}", taskId, task.getPetId());

                TaskCompletedEvent event = new TaskCompletedEvent(task.getId(), task.getPetId(), task.getTaskType().name(), task.getDescription(), TaskStatus.ESCALATED);
                taskEventPublisher.publishTaskCompletedEvent(event);

                return savedTask;
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
        log.info("Task deleted with ID: {}", id);
    }
}