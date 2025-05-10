package com.wolfhack.vetoptim.videoconsultation.service;

import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "videoNotificationTopic", "notification-video");

        // Default mock for successful scenarios
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void sendNotification_Success() {
        Long vetId = 1L;
        Long ownerId = 2L;
        String sessionId = "12345";
        NotificationType type = NotificationType.SESSION_START;
        String message = "Video session started for vetId: 1 and ownerId: 2. Session ID: 12345";
        String key = "session-12345";

        notificationService.sendNotification(vetId, ownerId, sessionId, type, null);

        verify(kafkaTemplate, times(1)).send("notification-video", key, message);
    }

    @Test
    void sendNotification_Failure_NoRetries() {
        Long vetId = 1L;
        Long ownerId = 2L;
        String sessionId = "12345";
        NotificationType type = NotificationType.SESSION_START;
        String key = "session-12345";

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka error", new RuntimeException()));
        when(kafkaTemplate.send(eq("notification-video"), eq(key), anyString()))
            .thenReturn((CompletableFuture) future);

        try {
            notificationService.sendNotification(vetId, ownerId, sessionId, type, null);
        } catch (RuntimeException e) {
            // Expected exception
        }

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }
}
