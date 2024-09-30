package com.wolfhack.vetoptim.taskresource.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationService notificationService;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testNotifyStaffOfUrgentTask() {
        Long taskId = 1L;
        String description = "Critical surgery task";

        notificationService.notifyStaffOfUrgentTask(taskId, description);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), contains("URGENT Task Alert!"));
    }

    @Test
    void testNotifyOfResourceDepletion() {
        String resourceName = "Surgical Kit";
        int remainingQuantity = 2;

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), contains("Resource Depletion Alert!"));
    }
}