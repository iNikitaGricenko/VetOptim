package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.pet}")
    private String petExchange;

    public void publishPetCreatedEvent(PetCreatedEvent event) {
        log.info("Publishing pet created event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(petExchange, "pet.created", event);
    }

    public void publishPetUpdatedEvent(PetUpdatedEvent event) {
        log.info("Publishing pet updated event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(petExchange, "pet.updated", event);
    }

    public void publishPetDeletedEvent(PetDeletedEvent event) {
        log.info("Publishing pet deleted event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(petExchange, "pet.deleted", event);
    }
}
