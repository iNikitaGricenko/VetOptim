package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
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
class AppointmentTaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private IAppointmentTaskEventPublisher IAppointmentTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(IAppointmentTaskEventPublisher, "appointmentTaskExchange", "appointment-task-exchange");
        ReflectionTestUtils.setField(IAppointmentTaskEventPublisher, "appointmentTaskRoutingKey", "task.appointment");
    }

    @Test
    void testPublishAppointmentTaskCreationEvent() {
        AppointmentTaskCreationEvent event = new AppointmentTaskCreationEvent(1L, 2L, "Buddy", "Dr. Smith", "Checkup", "Checkup for Buddy");

        IAppointmentTaskEventPublisher.publishAppointmentTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend("appointment-task-exchange", "task.appointment", event);
    }
}