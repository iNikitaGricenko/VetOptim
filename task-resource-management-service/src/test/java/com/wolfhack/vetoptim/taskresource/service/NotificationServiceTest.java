package com.wolfhack.vetoptim.taskresource.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
        notificationService = new NotificationService(rabbitTemplate);
        ReflectionTestUtils.setField(notificationService, "notificationExchange", "notification-exchange");
        ReflectionTestUtils.setField(notificationService, "urgentTaskRoutingKey", "notification.urgent");
        ReflectionTestUtils.setField(notificationService, "resourceDepletionRoutingKey", "notification.resource.depletion");
    }

    @Test
    void notifyStaffOfUrgentTask_Success() {
        Long taskId = 123L;
        String description = "Critical surgery needed";
        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);

        notificationService.notifyStaffOfUrgentTask(taskId, description);

        verify(rabbitTemplate, times(1)).convertAndSend("notification-exchange", "notification.urgent", message);
    }

    @Test
    void notifyOfResourceDepletion_Success() {
        String resourceName = "Vaccine";
        int remainingQuantity = 5;
        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(rabbitTemplate, times(1)).convertAndSend("notification-exchange", "notification.resource.depletion", message);
    }

    @Test
    void notifyStaffOfUrgentTask_Failure() {
        Long taskId = 123L;
        String description = "Critical surgery needed";
        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);

        doThrow(new RuntimeException("RabbitMQ error")).when(rabbitTemplate).convertAndSend("notification-exchange", "notification.urgent", message);

        notificationService.notifyStaffOfUrgentTask(taskId, description);

        verify(rabbitTemplate, times(1)).convertAndSend("notification-exchange", "notification.urgent", message);
    }

    @Test
    void notifyOfResourceDepletion_Failure() {
        String resourceName = "Vaccine";
        int remainingQuantity = 5;
        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);

        doThrow(new RuntimeException("RabbitMQ error")).when(rabbitTemplate).convertAndSend("notification-exchange", "notification.resource.depletion", message);

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(rabbitTemplate, times(1)).convertAndSend("notification-exchange", "notification.resource.depletion", message);
    }
}
