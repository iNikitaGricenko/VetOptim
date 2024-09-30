package com.wolfhack.vetoptim.owner.listener;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetEventListener {

    private final OwnerService ownerService;

    @RabbitListener(queues = "${rabbitmq.queue.pet.created}")
    public void handlePetCreatedEvent(PetCreatedEvent event) {
        log.info("Received PetCreatedEvent for Pet ID: {}", event.getPetId());

        ownerService.addPetToOwner(event.getOwnerId(), event.getPetId());

        log.info("Processed PetCreatedEvent for Pet ID: {}", event.getPetId());
    }
}