package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;
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

class PetEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PetEventPublisher petEventPublisher;

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
    void testPublishPetCreatedEvent() {
        PetCreatedEvent event = new PetCreatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);

        petEventPublisher.publishPetCreatedEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), eq("pet.created"), eq(event));
    }

    @Test
    void testPublishPetUpdatedEvent() {
        PetUpdatedEvent event = new PetUpdatedEvent(1L, "Buddy", "Dog", "Bulldog", 1L);

        petEventPublisher.publishPetUpdatedEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), eq("pet.updated"), eq(event));
    }

    @Test
    void testPublishPetDeletedEvent() {
        PetDeletedEvent event = new PetDeletedEvent(1L);

        petEventPublisher.publishPetDeletedEvent(event);

        verify(rabbitTemplate).convertAndSend(anyString(), eq("pet.deleted"), eq(event));
    }
}