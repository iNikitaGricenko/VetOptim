package com.wolfhack.vetoptim.taskresource.integration.scheduler;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.scheduler.TasksScheduler;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class TaskEscalationSchedulerIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private TasksScheduler tasksScheduler;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
    }

    @Test
    void testEscalateTasks_Success() {
        Task task = new Task();
        task.setPetId(1L);
        task.setTaskType(TaskType.SURGERY);
        task.setDescription("Urgent surgery for pet");
        task.setStatus(TaskStatus.PENDING);
        task.setDeadline(LocalDateTime.now().plusHours(12));
        task = taskRepository.save(task);

        doNothing().when(notificationService).notifyStaffOfUrgentTask(anyLong(), anyString());

        tasksScheduler.escalateTasks();

        Optional<Task> escalatedTask = taskRepository.findById(task.getId());
        assertTrue(escalatedTask.isPresent());
        assertEquals(TaskStatus.URGENT, escalatedTask.get().getStatus());

        verify(notificationService, times(1)).notifyStaffOfUrgentTask(anyLong(), anyString());
    }
}
