package com.wolfhack.vetoptim.appointment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.notification.appointment}")
    private String appointmentNotificationTopic;

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void notifyOwnerOfAppointment(String message) {
        log.info("Sending appointment notification: {}", message);
        String key = "appointment-notification";

        try {
            kafkaTemplate.send(appointmentNotificationTopic, key, message).get(10, TimeUnit.SECONDS);
            log.info("Appointment notification sent successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending appointment notification", e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to send appointment notification", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, String message) {
        log.error("Failed to send appointment notification after retries. Message: {}, Error: {}", 
                message, e.getMessage());
    }
}
