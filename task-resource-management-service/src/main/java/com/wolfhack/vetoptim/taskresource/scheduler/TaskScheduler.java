package com.wolfhack.vetoptim.taskresource.scheduler;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.taskresource.model.Task;
import com.wolfhack.vetoptim.taskresource.repository.TaskRepository;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * * * *")
    public void escalateTasks() {
        log.info("Starting task escalation process for tasks with deadlines in the next 24 hours.");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        List<Task> tasks = taskRepository.findAllByStatusAndDeadlineBetween(TaskStatus.PENDING, now, next24Hours);
        tasks.forEach(task -> {
            task.setStatus(TaskStatus.URGENT);
            taskRepository.save(task);
            notificationService.notifyStaffOfUrgentTask(task.getId(), task.getDescription());
            log.info("Task {} escalated to URGENT due to approaching deadline.", task.getId());
        });

        log.info("Task escalation process completed.");
    }
}