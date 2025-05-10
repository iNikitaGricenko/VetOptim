package com.wolfhack.vetoptim.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(kafkaTemplate);
        ReflectionTestUtils.setField(notificationService, "appointmentNotificationTopic", "notification-appointment");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void notifyOwnerOfAppointment_Success() {
        String message = "Appointment notification message";
        String key = "appointment-notification";

        notificationService.notifyOwnerOfAppointment(message);

        verify(kafkaTemplate).send(eq("notification-appointment"), eq(key), eq(message));
    }
}
