package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentTaskEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AppointmentTaskEventPublisher appointmentTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appointmentTaskEventPublisher, "appointmentTaskTopic", "task-appointment");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishAppointmentTaskCreationEvent() {
        AppointmentTaskCreationEvent event = new AppointmentTaskCreationEvent(1L, 2L, "Buddy", "Dr. Smith", "Checkup", "Checkup for Buddy");
        String key = "appointment-" + event.getAppointmentId();

        appointmentTaskEventPublisher.publishAppointmentTaskCreationEvent(event);

        verify(kafkaTemplate).send("task-appointment", key, event);
    }
}
