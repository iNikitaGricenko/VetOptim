package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VaccinationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.vaccination}")
    private String vaccinationExchange;

    @Value("${rabbitmq.routingKey.vaccination.reminder}")
    private String vaccinationReminderRoutingKey;

    public void publishVaccinationReminderEvent(VaccinationReminderEvent event) {
        log.info("Publishing vaccination reminder event for Pet ID: {}", event.getPetId());
        rabbitTemplate.convertAndSend(vaccinationExchange, vaccinationReminderRoutingKey, event);
    }
}
