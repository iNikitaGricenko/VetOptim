package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VaccinationEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VaccinationEventPublisher vaccinationEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(vaccinationEventPublisher, "vaccinationExchange", "vaccination-exchange");
        ReflectionTestUtils.setField(vaccinationEventPublisher, "vaccinationReminderRoutingKey", "vaccination.reminder");
    }

    @Test
    void testPublishVaccinationReminderEvent() {
        VaccinationReminderEvent event = new VaccinationReminderEvent(1L, "Buddy", 1L, "Rabies", "upcoming");

        vaccinationEventPublisher.publishVaccinationReminderEvent(event);

        verify(rabbitTemplate).convertAndSend("vaccination-exchange", "vaccination.reminder", event);
    }
}