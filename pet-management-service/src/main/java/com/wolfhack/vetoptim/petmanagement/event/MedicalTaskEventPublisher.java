package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalTaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.routingKey.task.medical}")
    private String taskRoutingKey;

    public void publishMedicalTaskCreationEvent(MedicalTaskCreationEvent event) {
        log.info("Publishing medical task creation event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(taskExchange, taskRoutingKey, event);
    }
}
