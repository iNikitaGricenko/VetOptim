package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.model.TaskHistory;
import com.wolfhack.vetoptim.taskresource.repository.TaskHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    public void logTaskChange(Task task, String description) {
        log.info("Logging task change for Task ID: {} with status: {}", task.getId(), task.getStatus());

        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getId());
        history.setDescription(description);
        history.setStatus(task.getStatus());
        history.setTimestamp(LocalDateTime.now());

        taskHistoryRepository.save(history);

        log.info("Task history logged for Task ID: {}", task.getId());
    }
}