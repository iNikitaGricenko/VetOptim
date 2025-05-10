package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.petmanagement.service.IPetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OwnerEventListener {

    private final IPetService petService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.owner.created}",
        groupId = "${kafka.consumer.group.owner}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOwnerCreatedEvent(
            @Payload OwnerCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received owner created event from topic {}, key {}: Owner ID = {}", 
                topic, key, event.getOwnerId());

        petService.updateOwnerInfoForPets(event.getOwnerId(), event.getOwnerName());

        log.info("Processed OwnerCreatedEvent for Owner ID: {}", event.getOwnerId());
    }
}
