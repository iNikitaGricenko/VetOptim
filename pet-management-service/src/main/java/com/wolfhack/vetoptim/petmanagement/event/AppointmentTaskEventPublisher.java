package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentTaskEventPublisher implements IAppointmentTaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.task.appointment}")
    private String appointmentTaskTopic;

    @Override
    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishAppointmentTaskCreationEvent(AppointmentTaskCreationEvent event) {
        log.info("Publishing appointment task creation event for Appointment ID: {}", event.getAppointmentId());
        String key = "appointment-" + event.getAppointmentId();

        try {
            kafkaTemplate.send(appointmentTaskTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Appointment task creation event published for Appointment ID: {}", event.getAppointmentId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing appointment task creation event for Appointment ID: {}", event.getAppointmentId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish appointment task creation event for Appointment ID: {}", event.getAppointmentId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, AppointmentTaskCreationEvent event) {
        log.error("Failed to publish appointment task creation event for Appointment ID: {} after retries. Error: {}", 
                event.getAppointmentId(), e.getMessage());
    }
}
