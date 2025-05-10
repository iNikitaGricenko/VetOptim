package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.IMedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusListener {

    private final IMedicalRecordService medicalRecordService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.task.completed}",
        groupId = "${kafka.consumer.group.task}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskCompleted(
            @Payload TaskCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received task completed event from topic {}, key {}: Task ID = {}, Pet ID = {}", 
                topic, key, event.getTaskId(), event.getPetId());

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
