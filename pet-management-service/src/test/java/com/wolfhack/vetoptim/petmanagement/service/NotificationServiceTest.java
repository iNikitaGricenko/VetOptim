package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

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
        ReflectionTestUtils.setField(notificationService, "appointmentNotificationRoutingKey", "notification.appointment");
        ReflectionTestUtils.setField(notificationService, "resourceDepletionRoutingKey", "notification.resource.depletion");
    }

    @Test
    void testNotifyOwnerOfAppointment_Success() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setVeterinarianName("Dr. John");
        appointmentDTO.setAppointmentDate(LocalDateTime.parse("2024-12-31T10:00"));

        String expectedMessage = "Appointment scheduled for pet ID: 1 with vet Dr. John on 2024-12-31T10:00.";

        notificationService.notifyOwnerOfAppointment(appointmentDTO);

        verify(rabbitTemplate).convertAndSend(
            eq("notification-exchange"),
            eq("notification.appointment"),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOwnerOfAppointment_Failure() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setVeterinarianName("Dr. John");
        appointmentDTO.setAppointmentDate(LocalDateTime.parse("2024-12-31T10:00"));

        String expectedMessage = "Appointment scheduled for pet ID: 1 with vet Dr. John on 2024-12-31T10:00.";

        doThrow(new AmqpException("RabbitMQ error")).when(rabbitTemplate)
            .convertAndSend("notification-exchange", "notification.appointment", expectedMessage);

        notificationService.notifyOwnerOfAppointment(appointmentDTO);

        verify(rabbitTemplate).convertAndSend(
            eq("notification-exchange"),
            eq("notification.appointment"),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOfResourceDepletion_Success() {
        String resourceName = "Vaccine";
        int remainingQuantity = 10;
        String expectedMessage = "Resource Depletion Alert: Resource Vaccine has 10 remaining.";

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(rabbitTemplate).convertAndSend(
            eq("notification-exchange"),
            eq("notification.resource.depletion"),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOfResourceDepletion_Failure() {
        String resourceName = "Vaccine";
        int remainingQuantity = 10;
        String expectedMessage = "Resource Depletion Alert: Resource Vaccine has 10 remaining.";

        doThrow(new AmqpException("RabbitMQ error")).when(rabbitTemplate)
            .convertAndSend("notification-exchange", "notification.resource.depletion", expectedMessage);

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(rabbitTemplate).convertAndSend(
            eq("notification-exchange"),
            eq("notification.resource.depletion"),
            eq(expectedMessage)
        );
    }
}