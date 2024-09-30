package com.wolfhack.vetoptim.owner.event;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OwnerEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routingKey.owner.created}")
    private String ownerCreatedRoutingKey;

    @Value("${rabbitmq.routingKey.owner.updated}")
    private String ownerUpdatedRoutingKey;

    @Value("${rabbitmq.routingKey.owner.deleted}")
    private String ownerDeletedRoutingKey;

    public void publishOwnerCreatedEvent(OwnerCreatedEvent event) {
        log.info("Publishing owner created event for owner ID: {}", event.getOwnerId());
        rabbitTemplate.convertAndSend(ownerExchange, ownerCreatedRoutingKey, event);
    }

    public void publishOwnerUpdatedEvent(OwnerUpdatedEvent event) {
        log.info("Publishing owner updated event for owner ID: {}", event.getOwnerId());
        rabbitTemplate.convertAndSend(ownerExchange, ownerUpdatedRoutingKey, event);
    }

    public void publishOwnerDeletedEvent(OwnerDeletedEvent event) {
        log.info("Publishing owner deleted event for owner ID: {}", event.getOwnerId());
        rabbitTemplate.convertAndSend(ownerExchange, ownerDeletedRoutingKey, event);
    }
}