package com.wolfhack.vetoptim.appointment.listener;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
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
public class TaskCompletionListener {

    private final AppointmentService appointmentService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.task.completed}",
        groupId = "${kafka.consumer.group.task}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskCompletion(
            @Payload TaskCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received task completion event for Task ID: {}", event.getTaskId());

        try {
            if (event.getStatus() == TaskStatus.COMPLETED) {
                appointmentService.updateAppointmentStatus(event.getTaskId(), AppointmentStatus.COMPLETED);
                log.info("Updated appointment status to COMPLETED for appointment ID: {}", event.getTaskId());
            } else if (event.getStatus() == TaskStatus.FAILED) {
                appointmentService.updateAppointmentStatus(event.getTaskId(), AppointmentStatus.CANCELED);
                log.info("Updated appointment status to CANCELED for appointment ID: {}", event.getTaskId());
            } else if (event.getStatus() == TaskStatus.ESCALATED) {
                appointmentService.updateAppointmentStatus(event.getTaskId(), AppointmentStatus.ESCALATED);
                log.info("Updated appointment status to ESCALATED for appointment ID: {}", event.getTaskId());
            }
        } catch (Exception e) {
            log.error("Failed to update appointment status for appointment ID: {}", event.getTaskId(), e);
        }
    }
}
