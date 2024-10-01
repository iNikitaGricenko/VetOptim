package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppointmentEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AppointmentEventPublisher appointmentEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appointmentEventPublisher, "taskExchange", "task-exchange");
        ReflectionTestUtils.setField(appointmentEventPublisher, "appointmentTaskRoutingKey", "task.appointment");
    }

    @Test
    void publishAppointmentTaskCreationEvent_Success() {
        AppointmentTaskCreationEvent event = new AppointmentTaskCreationEvent(1L, 100L, "Buddy", "Dr. Smith", "Checkup", "2024-10-01T10:00");

        appointmentEventPublisher.publishAppointmentTaskCreationEvent(event);

        verify(rabbitTemplate, times(1)).convertAndSend("task-exchange", "task.appointment", event);
    }
}
