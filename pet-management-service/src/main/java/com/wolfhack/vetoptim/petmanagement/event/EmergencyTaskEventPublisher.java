package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmergencyTaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.routingKey.task.emergency}")
    private String emergencyTaskRoutingKey;

    public void publishEmergencyTaskCreationEvent(EmergencyTaskCreationEvent event) {
        log.info("Publishing emergency task creation event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(taskExchange, emergencyTaskRoutingKey, event);
    }
}