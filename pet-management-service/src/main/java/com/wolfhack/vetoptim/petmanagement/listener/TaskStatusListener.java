package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusListener {

    private final MedicalRecordService medicalRecordService;

    @Async
    @RabbitListener(queues = "${rabbitmq.queue.task.completed}")
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Received TaskCompletedEvent for Task ID: {} for Pet ID: {}", event.getTaskId(), event.getPetId());

        if ("Surgery".equalsIgnoreCase(event.getTaskType())) {
            log.info("Handling task completion for surgery. Updating medical record for Pet ID: {}", event.getPetId());
            medicalRecordService.createMedicalRecordFromAppointment(
                event.getPetId(),
                "Surgery Completed",
                event.getDescription()
            );
        }

        log.info("Task completion handling completed for Task ID: {}", event.getTaskId());
    }
}