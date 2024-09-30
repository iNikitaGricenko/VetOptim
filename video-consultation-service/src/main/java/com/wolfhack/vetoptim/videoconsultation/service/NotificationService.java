package com.wolfhack.vetoptim.videoconsultation.service;

import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routingKey.notification.video}")
    private String videoNotificationRoutingKey;

    @Retryable(
        retryFor = {AmqpException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void sendNotification(Long vetId, Long ownerId, String sessionId, NotificationType type, String additionalMessage) {
        String message = buildNotificationMessage(vetId, ownerId, sessionId, type, additionalMessage);
        log.info("Sending {} notification: {}", type, message);

        try {
            rabbitTemplate.convertAndSend(notificationExchange, videoNotificationRoutingKey, message);
            log.info("Notification for {} sent successfully.", type);
        } catch (AmqpException e) {
            log.error("Failed to send {} notification. Retrying... Error: {}", type, e.getMessage());
            throw e;
        }
    }

    @Recover
    public void recover(AmqpException e, Long vetId, Long ownerId, String sessionId, NotificationType type, String additionalMessage) {
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