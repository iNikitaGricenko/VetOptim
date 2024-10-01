package com.wolfhack.vetoptim.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

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
        ReflectionTestUtils.setField(notificationService, "appointmentNotificationRoutingKey", "appointment-notification-key");
    }

    @Test
    void notifyOwnerOfAppointment_Success() {
        String message = "Appointment notification message";
        notificationService.notifyOwnerOfAppointment(message);

	    verify(rabbitTemplate).convertAndSend("notification-exchange", "appointment-notification-key", message);
    }
}
