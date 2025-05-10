package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AppointmentEventPublisher appointmentEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appointmentEventPublisher, "taskAppointmentTopic", "task-appointment");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
    }

    @Test
    void publishAppointmentTaskCreationEvent_Success() {
        AppointmentTaskCreationEvent event = new AppointmentTaskCreationEvent(1L, 100L, "Buddy", "Dr. Smith", "Checkup", "2024-10-01T10:00");
        String key = "appointment-" + event.getAppointmentId();

        appointmentEventPublisher.publishAppointmentTaskCreationEvent(event);

        verify(kafkaTemplate, times(1)).send("task-appointment", key, event);
    }
}
