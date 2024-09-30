package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
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

class VaccinationEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VaccinationEventPublisher vaccinationEventPublisher;

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
    void testPublishVaccinationReminderEvent() {
        VaccinationReminderEvent event = new VaccinationReminderEvent(1L, "Buddy", 1L, "Rabies", "upcoming");

        vaccinationEventPublisher.publishVaccinationReminderEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), eq("vaccination.reminder"), eq(event));
    }
}