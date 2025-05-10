package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetDeletedEvent;
import com.wolfhack.vetoptim.common.event.pet.PetUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetEventPublisher implements IPetEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.pet.created}")
    private String petCreatedTopic;

    @Value("${kafka.topic.pet.updated}")
    private String petUpdatedTopic;

    @Value("${kafka.topic.pet.deleted}")
    private String petDeletedTopic;

    @Override
    public void publishPetCreatedEvent(PetCreatedEvent event) {
        log.info("Publishing pet created event for Pet ID: {}", event.getPetId());
        String key = "pet-" + event.getPetId();
        kafkaTemplate.send(petCreatedTopic, key, event);
        log.debug("Pet created event sent to topic: {}", petCreatedTopic);
    }

    @Override
    public void publishPetUpdatedEvent(PetUpdatedEvent event) {
        log.info("Publishing pet updated event for Pet ID: {}", event.getPetId());
        String key = "pet-" + event.getPetId();
        kafkaTemplate.send(petUpdatedTopic, key, event);
        log.debug("Pet updated event sent to topic: {}", petUpdatedTopic);
    }

    @Override
    public void publishPetDeletedEvent(PetDeletedEvent event) {
        log.info("Publishing pet deleted event for Pet ID: {}", event.getPetId());
        String key = "pet-" + event.getPetId();
        kafkaTemplate.send(petDeletedTopic, key, event);
        log.debug("Pet deleted event sent to topic: {}", petDeletedTopic);
    }
}
