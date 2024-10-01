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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PetEventPublisher petEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(petEventPublisher, "petExchange", "pet-exchange");
    }

    @Test
    void testPublishPetCreatedEvent() {
        PetCreatedEvent event = new PetCreatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);

        petEventPublisher.publishPetCreatedEvent(event);

        verify(rabbitTemplate).convertAndSend("pet-exchange", "pet.created", event);
    }

    @Test
    void testPublishPetUpdatedEvent() {
        PetUpdatedEvent event = new PetUpdatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);

        petEventPublisher.publishPetUpdatedEvent(event);

        verify(rabbitTemplate).convertAndSend("pet-exchange", "pet.updated", event);
    }

    @Test
    void testPublishPetDeletedEvent() {
        PetDeletedEvent event = new PetDeletedEvent(1L);

        petEventPublisher.publishPetDeletedEvent(event);

        verify(rabbitTemplate).convertAndSend("pet-exchange", "pet.deleted", event);
    }
}