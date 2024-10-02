package com.wolfhack.vetoptim.taskresource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(
        retryFor = {AmqpException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void notifyStaffOfUrgentTask(Long taskId, String description) {
        log.info("Notifying staff: Task {} is URGENT. Description: {}", taskId, description);

        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);

        rabbitTemplate.convertAndSend(notificationExchange, urgentTaskRoutingKey, message);
        log.info("Urgent task notification sent for Task ID: {}", taskId);
    }

    @Retryable(
        retryFor = {AmqpException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void notifyOfResourceDepletion(String resourceName, int remainingQuantity) {
        log.info("Notifying staff: Resource {} is running low. Remaining quantity: {}", resourceName, remainingQuantity);

        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);

        rabbitTemplate.convertAndSend(notificationExchange, resourceDepletionRoutingKey, message);
        log.info("Resource depletion notification sent for Resource: {}", resourceName);
    }

    @Recover
    public void recover(AmqpException e, Long taskId, String description) {
        log.error("Failed to send urgent task notification for Task ID: {}. Error: {}", taskId, e.getMessage());
    }

    @Recover
    public void recover(AmqpException e, String resourceName, int remainingQuantity) {
        log.error("Failed to send resource depletion notification for Resource: {}. Error: {}", resourceName, e.getMessage());
    }
}