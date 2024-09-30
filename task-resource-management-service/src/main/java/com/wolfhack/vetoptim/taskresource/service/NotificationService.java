package com.wolfhack.vetoptim.taskresource.service;

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

    @Value("${rabbitmq.routingKey.notification.urgent}")
    private String urgentTaskRoutingKey;

    @Value("${rabbitmq.routingKey.notification.resource.depletion}")
    private String resourceDepletionRoutingKey;

    public void notifyStaffOfUrgentTask(Long taskId, String description) {
        log.info("Notifying staff: Task {} is URGENT. Description: {}", taskId, description);

        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);

        try {
            rabbitTemplate.convertAndSend(notificationExchange, urgentTaskRoutingKey, message);
            log.info("Urgent task notification sent for Task ID: {}", taskId);
        } catch (Exception e) {
            log.error("Failed to send urgent task notification for Task ID: {}. Error: {}", taskId, e.getMessage());
        }
    }

    public void notifyOfResourceDepletion(String resourceName, int remainingQuantity) {
        log.info("Notifying staff: Resource {} is running low. Remaining quantity: {}", resourceName, remainingQuantity);

        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);

        try {
            rabbitTemplate.convertAndSend(notificationExchange, resourceDepletionRoutingKey, message);
            log.info("Resource depletion notification sent for Resource: {}", resourceName);
        } catch (Exception e) {
            log.error("Failed to send resource depletion notification for Resource: {}. Error: {}", resourceName, e.getMessage());
        }
    }
}