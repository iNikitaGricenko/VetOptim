package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class EmergencyTaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmergencyTaskEventPublisher emergencyTaskEventPublisher;

	private AutoCloseable autoCloseable;

	@BeforeEach
    void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testPublishEmergencyTaskCreationEvent() {
        EmergencyTaskCreationEvent event = new EmergencyTaskCreationEvent(1L, "PetName", "Critical", "Emergency procedure");

        emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(event));
    }
}