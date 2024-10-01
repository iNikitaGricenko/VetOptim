package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

import com.wolfhack.vetoptim.appointment.event.AppointmentEventPublisher;
import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

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
