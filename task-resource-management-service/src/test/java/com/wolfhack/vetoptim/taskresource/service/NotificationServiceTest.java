package com.wolfhack.vetoptim.taskresource.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(kafkaTemplate);
        ReflectionTestUtils.setField(notificationService, "urgentTaskTopic", "notification-urgent");
        ReflectionTestUtils.setField(notificationService, "resourceDepletionTopic", "notification-resource-depletion");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void notifyStaffOfUrgentTask_Success() {
        Long taskId = 123L;
        String description = "Critical surgery needed";
        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);
        String key = "task-" + taskId;

        notificationService.notifyStaffOfUrgentTask(taskId, description);

        verify(kafkaTemplate, times(1)).send("notification-urgent", key, message);
    }

    @Test
    void notifyOfResourceDepletion_Success() {
        String resourceName = "Vaccine";
        int remainingQuantity = 5;
        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);
        String key = "resource-" + resourceName;

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(kafkaTemplate, times(1)).send("notification-resource-depletion", key, message);
    }
}
