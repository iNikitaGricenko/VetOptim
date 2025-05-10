package com.wolfhack.vetoptim.owner.listener;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetEventListener {

    private final OwnerService ownerService;

    @KafkaListener(
        topics = "${kafka.topic.pet.created}",
        groupId = "${kafka.consumer.group.pet}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePetCreatedEvent(
            @Payload PetCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received PetCreatedEvent for Pet ID: {}", event.getPetId());

        ownerService.addPetToOwner(event.getOwnerId(), event.getPetId());

        log.info("Processed PetCreatedEvent for Pet ID: {}", event.getPetId());
    }
}
