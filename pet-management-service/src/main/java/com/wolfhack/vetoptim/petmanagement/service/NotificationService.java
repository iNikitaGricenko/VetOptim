package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routingKey.notification.appointment}")
    private String appointmentNotificationRoutingKey;

    @Value("${rabbitmq.routingKey.notification.resource.depletion}")
    private String resourceDepletionRoutingKey;

    public void notifyOwnerOfAppointment(AppointmentDTO appointment) {
        String message = String.format("Appointment scheduled for pet %s with vet %s on %s.",
            appointment.getPetId(), appointment.getVeterinarianName(), appointment.getAppointmentDate());
        log.info("Notifying owner about appointment: {}", message);

        try {
            rabbitTemplate.convertAndSend(notificationExchange, appointmentNotificationRoutingKey, message);
            log.info("Notification sent for appointment.");
        } catch (Exception e) {
            log.error("Failed to send notification for appointment. Error: {}", e.getMessage());
        }
    }

    public void notifyOfResourceDepletion(String resourceName, int remainingQuantity) {
        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);
        log.info("Notifying staff about resource depletion: {}", message);

        try {
            rabbitTemplate.convertAndSend(notificationExchange, resourceDepletionRoutingKey, message);
            log.info("Resource depletion notification sent.");
        } catch (Exception e) {
            log.error("Failed to send resource depletion notification. Error: {}", e.getMessage());
        }
    }
}
