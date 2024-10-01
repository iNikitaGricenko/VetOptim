package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskScheduler taskScheduler;

    @Test
    void testEscalateTasks() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus(TaskStatus.PENDING);
        task1.setDeadline(LocalDateTime.now().plusHours(12));
        task1.setDescription("Task 1 description");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus(TaskStatus.PENDING);
        task2.setDeadline(LocalDateTime.now().plusHours(10));
        task2.setDescription("Task 2 description");

        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findAllByStatusAndDeadlineBetween(any(TaskStatus.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(tasks);

        taskScheduler.escalateTasks();

        verify(taskRepository, times(2)).save(any(Task.class));
        verify(notificationService).notifyStaffOfUrgentTask(task1.getId(), task1.getDescription());
        verify(notificationService).notifyStaffOfUrgentTask(task2.getId(), task2.getDescription());

        assertEquals(TaskStatus.URGENT, task1.getStatus());
        assertEquals(TaskStatus.URGENT, task2.getStatus());
    }
}
