package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.dto.PetDTO;
import com.wolfhack.vetoptim.common.dto.TaskDTO;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.client.PetClient;
import com.wolfhack.vetoptim.taskresource.event.TaskEventPublisher;
import com.wolfhack.vetoptim.taskresource.mapper.TaskMapper;
import com.wolfhack.vetoptim.taskresource.model.ResourceUsage;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private PetClient petClient;

    @Mock
    private BillingClient billingClient;

    @Mock
    private TaskEventPublisher taskEventPublisher;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testGetAllTasks() {
        taskService.getAllTasks();
        verify(taskRepository).findAll();
    }

    @Test
    void testGetTaskById() {
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(new Task()));

        Optional<Task> result = taskService.getTaskById(id);

        assertTrue(result.isPresent());
        verify(taskRepository).findById(id);
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setPetId(1L);

        when(petClient.getPetById(1L)).thenReturn(new PetDTO());
        when(taskRepository.save(any())).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        verify(taskRepository).save(task);
        verify(taskEventPublisher).publishTaskCreatedEvent(any());
    }

    @Test
    void testCompleteTask() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.PENDING);
        task.setPetId(123L);

        ResourceUsage resourceUsage = new ResourceUsage(1L, "Surgical Kit", 1);
        task.addResourceUsage(resourceUsage);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task completedTask = taskService.completeTask(1L);

        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(billingClient).sendTaskBillingRequest(any(TaskBillingRequest.class));
        verify(taskEventPublisher).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testFailTask() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task failedTask = taskService.failTask(1L);

        assertThat(failedTask.getStatus()).isEqualTo(TaskStatus.FAILED);
        verify(taskEventPublisher).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testEscalateTask() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task escalatedTask = taskService.escalateTask(1L);

        assertThat(escalatedTask.getStatus()).isEqualTo(TaskStatus.ESCALATED);
        verify(taskEventPublisher).publishTaskCompletedEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void testUpdateTask() {
        Long id = 1L;
        TaskDTO taskDTO = new TaskDTO();
        Task task = new Task();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        Task updatedTask = taskService.updateTask(id, taskDTO);

        assertNotNull(updatedTask);
        verify(taskMapper).updateTaskFromDTO(taskDTO, task);
        verify(taskRepository).save(task);
    }

    @Test
    void testDeleteTask() {
        Long id = 1L;
        taskService.deleteTask(id);
        verify(taskRepository).deleteById(id);
    }
}