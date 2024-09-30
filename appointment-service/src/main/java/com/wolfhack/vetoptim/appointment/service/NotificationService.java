package com.wolfhack.vetoptim.appointment.service;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routingKey.notification.appointment}")
    private String appointmentNotificationRoutingKey;

    public void notifyOwnerOfAppointment(String message) {
        rabbitTemplate.convertAndSend(notificationExchange, appointmentNotificationRoutingKey, message);
    }
}