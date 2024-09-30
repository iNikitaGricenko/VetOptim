package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

class AppointmentEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AppointmentEventPublisher appointmentEventPublisher;

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
    void testPublishAppointmentTaskCreationEvent() {
        AppointmentTaskCreationEvent event = new AppointmentTaskCreationEvent(1L, 1L, "Fluffy", "Dr. Smith", "Checkup", "2024-09-30T10:00:00");

        appointmentEventPublisher.publishAppointmentTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(event));
    }
}