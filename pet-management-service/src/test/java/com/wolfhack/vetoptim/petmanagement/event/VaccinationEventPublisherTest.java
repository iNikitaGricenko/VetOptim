package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
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
class VaccinationEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VaccinationEventPublisher IVaccinationEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(IVaccinationEventPublisher, "vaccinationExchange", "vaccination-exchange");
        ReflectionTestUtils.setField(IVaccinationEventPublisher, "vaccinationReminderRoutingKey", "vaccination.reminder");
    }

    @Test
    void testPublishVaccinationReminderEvent() {
        VaccinationReminderEvent event = new VaccinationReminderEvent(1L, "Buddy", 1L, "Rabies", "upcoming");

        IVaccinationEventPublisher.publishVaccinationReminderEvent(event);

        verify(rabbitTemplate).convertAndSend("vaccination-exchange", "vaccination.reminder", event);
    }
}