package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.model.TaskHistory;
import com.wolfhack.vetoptim.taskresource.repository.TaskHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskHistoryServiceTest {

    @Mock
    private TaskHistoryRepository taskHistoryRepository;

    @InjectMocks
    private TaskHistoryService taskHistoryService;

    @Test
    void logTaskChange_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.IN_PROGRESS);

        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getId());
        history.setDescription("Task created");
        history.setStatus(task.getStatus());
        history.setTimestamp(LocalDateTime.now());

        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(history);

        taskHistoryService.logTaskChange(task, "Task created");

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }
}
