package com.wolfhack.vetoptim.appointment.service;

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
    void testNotifyOwnerOfAppointment() {
        String message = "Appointment scheduled...";

        notificationService.notifyOwnerOfAppointment(message);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(message));
    }
}