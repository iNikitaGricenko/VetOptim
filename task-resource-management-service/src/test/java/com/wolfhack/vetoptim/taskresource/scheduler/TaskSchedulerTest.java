package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class TaskSchedulerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskScheduler taskScheduler;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
    void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testEscalateTasks() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.PENDING);
        task.setDeadline(LocalDateTime.now().plusHours(1));

        when(taskRepository.findAllByStatusAndDeadlineBetween(any(), any(), any())).thenReturn(List.of(task));

        taskScheduler.escalateTasks();

        verify(taskRepository).findAllByStatusAndDeadlineBetween(any(), any(), any());
        verify(taskRepository).save(task);
        verify(notificationService).notifyStaffOfUrgentTask(task.getId(), task.getDescription());
    }
}