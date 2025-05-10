package com.wolfhack.vetoptim.videoconsultation.service;

import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
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

    @Value("${kafka.topic.notification.video}")
    private String videoNotificationTopic;

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void sendNotification(Long vetId, Long ownerId, String sessionId, NotificationType type, String additionalMessage) {
        String message = buildNotificationMessage(vetId, ownerId, sessionId, type, additionalMessage);
        log.info("Sending {} notification: {}", type, message);
        String key = "session-" + sessionId;

        try {
            kafkaTemplate.send(videoNotificationTopic, key, message).get(10, TimeUnit.SECONDS);
            log.info("Notification for {} sent successfully.", type);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending {} notification for session ID: {}", type, sessionId, e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to send {} notification for session ID: {}. Error: {}", type, sessionId, e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, Long vetId, Long ownerId, String sessionId, NotificationType type, String additionalMessage) {
        log.error("All retries failed for sending {} notification for session ID: {}. Error: {}", type, sessionId, e.getMessage());
    }

    private String buildNotificationMessage(Long vetId, Long ownerId, String sessionId, NotificationType type, String additionalMessage) {
        return switch (type) {
            case SESSION_START ->
                String.format("Video session started for vetId: %d and ownerId: %d. Session ID: %s", vetId, ownerId, sessionId);
            case SESSION_END -> String.format("Video session ended for session ID: %s", sessionId);
            case RECORDING_UPLOADED ->
                String.format("Recording uploaded for session ID: %s. %s", sessionId, additionalMessage);
            case TRANSCODING_COMPLETED ->
                String.format("Transcoding completed for session ID: %s. Video available at: %s", sessionId, additionalMessage);
            default -> "Unknown notification type.";
        };
    }
}
