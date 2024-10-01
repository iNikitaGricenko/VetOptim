package com.wolfhack.vetoptim.videoconsultation.service;

import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "notificationExchange", "notification-exchange");
        ReflectionTestUtils.setField(notificationService, "videoNotificationRoutingKey", "video.notification");
    }

    @Test
    void sendNotification_Success() {
        Long vetId = 1L;
        Long ownerId = 2L;
        String sessionId = "12345";
        NotificationType type = NotificationType.SESSION_START;
        String message = "Video session started for vetId: 1 and ownerId: 2. Session ID: 12345";

        notificationService.sendNotification(vetId, ownerId, sessionId, type, null);

        verify(rabbitTemplate, times(1)).convertAndSend("notification-exchange", "video.notification", message);
    }

    @Test
    void sendNotification_Failure_NoRetries() {
        Long vetId = 1L;
        Long ownerId = 2L;
        String sessionId = "12345";
        NotificationType type = NotificationType.SESSION_START;

        doThrow(new AmqpException("RabbitMQ error"))
            .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        try {
            notificationService.sendNotification(vetId, ownerId, sessionId, type, null);
        } catch (AmqpException e) {
        }

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), anyString());
    }
}
