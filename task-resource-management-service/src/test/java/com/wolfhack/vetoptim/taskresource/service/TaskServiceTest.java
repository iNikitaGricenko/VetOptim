package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
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
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ResourceUsedMapper resourceUsedMapper;

    @Mock
    private PetClient petClient;

    @Mock
    private BillingClient billingClient;

    @Mock
    private TaskEventPublisher taskEventPublisher;

    @Mock
    private ResourceAllocationService resourceAllocationService;

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private WorkloadBalancingService workloadBalancingService;

    @Mock
    private TaskAssignmentService taskAssignmentService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setPetId(100L);
        task.setTaskType(TaskType.CHECKUP);
        task.setDescription("Initial Task");

        taskDTO = new TaskDTO();
        taskDTO.setPetId(100L);
        taskDTO.setTaskType(TaskType.CHECKUP);
        taskDTO.setDescription("Initial Task");
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        Optional<TaskDTO> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(taskDTO, result.get());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateTask() {
        when(taskMapper.toModel(any(TaskDTO.class))).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);
        when(petClient.getPetById(taskDTO.getPetId())).thenReturn(new PetDTO());

        TaskDTO createdTask = taskService.createTask(taskDTO);

        assertNotNull(createdTask);
        assertEquals(taskDTO.getId(), createdTask.getId());
        verify(taskRepository, times(1)).save(task);
        verify(taskEventPublisher, times(1)).publishTaskCreatedEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void testUpdateTask() {
        taskDTO.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        TaskDTO updatedTask = taskService.updateTask(1L, taskDTO);

        assertNotNull(updatedTask);
        verify(taskMapper, times(1)).updateTaskFromDTO(eq(taskDTO), any(Task.class));
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskEventPublisher, times(1)).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testCompleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskDTO.setStatus(TaskStatus.COMPLETED);

        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        TaskDTO completedTask = taskService.completeTask(1L);

        assertNotNull(completedTask);
        assertEquals(TaskStatus.COMPLETED, completedTask.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(billingClient, times(1)).sendTaskBillingRequest(any(TaskBillingRequest.class));
        verify(taskEventPublisher, times(1)).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testFailTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskDTO.setStatus(TaskStatus.FAILED);

        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        TaskDTO failedTask = taskService.failTask(1L);

        assertNotNull(failedTask);
        assertEquals(TaskStatus.FAILED, failedTask.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(taskEventPublisher, times(1)).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testEscalateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskDTO.setStatus(TaskStatus.ESCALATED);

        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        TaskDTO escalatedTask = taskService.escalateTask(1L);

        assertNotNull(escalatedTask);
        assertEquals(TaskStatus.ESCALATED, escalatedTask.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(taskEventPublisher, times(1)).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }
}
