package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OwnerEventListener {

    private final PetService petService;

    @RabbitListener(queues = "${rabbitmq.queue.owner.created}")
    public void handleOwnerCreatedEvent(OwnerCreatedEvent event) {
        log.info("Received OwnerCreatedEvent for Owner ID: {}", event.getOwnerId());

        petService.updateOwnerInfoForPets(event.getOwnerId(), event.getOwnerName());

        log.info("Processed OwnerCreatedEvent for Owner ID: {}", event.getOwnerId());
    }
}