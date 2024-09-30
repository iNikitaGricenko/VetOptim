package com.wolfhack.vetoptim.videoconsultation.service;

import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testSendNotification_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        notificationService.sendNotification(1L, 1L, "testSessionId", NotificationType.SESSION_START, null);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void testSendNotification_FailureAndRetry() {
        doThrow(AmqpException.class).when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        assertThrows(AmqpException.class, () -> notificationService.sendNotification(1L, 1L, "testSessionId", NotificationType.SESSION_START, null));

        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void testRecoverAfterFailure() {
        notificationService.recover(new AmqpException("error"), 1L, 1L, "testSessionId", NotificationType.SESSION_START, null);

        verifyNoInteractions(rabbitTemplate);
    }
}
