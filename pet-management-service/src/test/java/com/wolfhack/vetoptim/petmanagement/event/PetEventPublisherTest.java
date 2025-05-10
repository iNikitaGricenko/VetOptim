package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;
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
class PetEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PetEventPublisher petEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(petEventPublisher, "petCreatedTopic", "pet-created");
        ReflectionTestUtils.setField(petEventPublisher, "petUpdatedTopic", "pet-updated");
        ReflectionTestUtils.setField(petEventPublisher, "petDeletedTopic", "pet-deleted");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishPetCreatedEvent() {
        PetCreatedEvent event = new PetCreatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);
        String key = "pet-" + event.getPetId();

        petEventPublisher.publishPetCreatedEvent(event);

        verify(kafkaTemplate).send("pet-created", key, event);
    }

    @Test
    void testPublishPetUpdatedEvent() {
        PetUpdatedEvent event = new PetUpdatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);
        String key = "pet-" + event.getPetId();

        petEventPublisher.publishPetUpdatedEvent(event);

        verify(kafkaTemplate).send("pet-updated", key, event);
    }

    @Test
    void testPublishPetDeletedEvent() {
        PetDeletedEvent event = new PetDeletedEvent(1L);
        String key = "pet-" + event.getPetId();

        petEventPublisher.publishPetDeletedEvent(event);

        verify(kafkaTemplate).send("pet-deleted", key, event);
    }
}
