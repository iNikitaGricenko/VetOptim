package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentTaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.appointment-task}")
    private String appointmentTaskExchange;

    @Value("${rabbitmq.routingKey.task.appointment}")
    private String appointmentTaskRoutingKey;

    public void publishAppointmentTaskCreationEvent(AppointmentTaskCreationEvent event) {
        log.info("Publishing appointment task creation event for Appointment ID: {}", event.getAppointmentId());
        rabbitTemplate.convertAndSend(appointmentTaskExchange, appointmentTaskRoutingKey, event);
    }
}