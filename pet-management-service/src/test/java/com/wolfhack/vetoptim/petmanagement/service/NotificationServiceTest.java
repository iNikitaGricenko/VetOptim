package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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
        ReflectionTestUtils.setField(notificationService, "appointmentNotificationTopic", "notification-appointment");
        ReflectionTestUtils.setField(notificationService, "resourceDepletionTopic", "notification-resource-depletion");

        // Default mock for successful scenarios
        // Use lenient() to allow unused stubbings
        lenient().when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testNotifyOwnerOfAppointment_Success() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(123L);
        appointmentDTO.setPetId(1L);
        appointmentDTO.setVeterinarianName("Dr. John");
        appointmentDTO.setAppointmentDate(LocalDateTime.parse("2024-12-31T10:00"));

        String expectedMessage = "Appointment scheduled for pet ID: 1 with vet Dr. John on 2024-12-31T10:00.";
        String expectedKey = "appointment-123";

        notificationService.notifyOwnerOfAppointment(appointmentDTO);

        verify(kafkaTemplate).send(
            eq("notification-appointment"),
            eq(expectedKey),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOwnerOfAppointment_Failure() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(123L);
        appointmentDTO.setPetId(1L);
        appointmentDTO.setVeterinarianName("Dr. John");
        appointmentDTO.setAppointmentDate(LocalDateTime.parse("2024-12-31T10:00"));

        String expectedMessage = "Appointment scheduled for pet ID: 1 with vet Dr. John on 2024-12-31T10:00.";
        String expectedKey = "appointment-123";

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka error", new RuntimeException()));
        // Use lenient() to allow unused stubbings
        lenient().when(kafkaTemplate.send(eq("notification-appointment"), eq(expectedKey), eq(expectedMessage)))
            .thenReturn((CompletableFuture) future);

        notificationService.notifyOwnerOfAppointment(appointmentDTO);

        verify(kafkaTemplate).send(
            eq("notification-appointment"),
            eq(expectedKey),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOfResourceDepletion_Success() {
        String resourceName = "Vaccine";
        int remainingQuantity = 10;
        String expectedMessage = "Resource Depletion Alert: Resource Vaccine has 10 remaining.";
        String expectedKey = "resource-Vaccine";

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(kafkaTemplate).send(
            eq("notification-resource-depletion"),
            eq(expectedKey),
            eq(expectedMessage)
        );
    }

    @Test
    void testNotifyOfResourceDepletion_Failure() {
        String resourceName = "Vaccine";
        int remainingQuantity = 10;
        String expectedMessage = "Resource Depletion Alert: Resource Vaccine has 10 remaining.";
        String expectedKey = "resource-Vaccine";

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka error", new RuntimeException()));
        // Use lenient() to allow unused stubbings
        lenient().when(kafkaTemplate.send(eq("notification-resource-depletion"), eq(expectedKey), eq(expectedMessage)))
            .thenReturn((CompletableFuture) future);

        notificationService.notifyOfResourceDepletion(resourceName, remainingQuantity);

        verify(kafkaTemplate).send(
            eq("notification-resource-depletion"),
            eq(expectedKey),
            eq(expectedMessage)
        );
    }
}
