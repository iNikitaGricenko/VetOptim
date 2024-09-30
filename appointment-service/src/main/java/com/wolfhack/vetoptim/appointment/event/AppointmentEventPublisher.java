package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.routingKey.task.appointment}")
    private String appointmentTaskRoutingKey;

    public void publishAppointmentTaskCreationEvent(AppointmentTaskCreationEvent event) {
        rabbitTemplate.convertAndSend(taskExchange, appointmentTaskRoutingKey, event);
    }
}