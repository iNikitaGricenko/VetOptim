package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
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
class VaccinationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private VaccinationEventPublisher vaccinationEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(vaccinationEventPublisher, "vaccinationReminderTopic", "vaccination-reminder");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishVaccinationReminderEvent() {
        VaccinationReminderEvent event = new VaccinationReminderEvent(1L, "Buddy", 1L, "Rabies", "upcoming");
        String key = "pet-" + event.getPetId();

        vaccinationEventPublisher.publishVaccinationReminderEvent(event);

        verify(kafkaTemplate).send("vaccination-reminder", key, event);
    }
}
