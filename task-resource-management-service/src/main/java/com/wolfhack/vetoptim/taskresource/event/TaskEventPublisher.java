package com.wolfhack.vetoptim.taskresource.event;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.task}")
    private String taskExchange;

    @Value("${rabbitmq.routingKey.task.created}")
    private String taskCreatedRoutingKey;

    @Value("${rabbitmq.routingKey.task.completed}")
    private String taskCompletedRoutingKey;

    @Value("${rabbitmq.routingKey.resource.depleted}")
    private String resourceDepletedRoutingKey;

    @Retryable(
        retryFor = { AmqpException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishTaskCreatedEvent(TaskCreatedEvent event) {
        log.info("Publishing task created event for Task ID: {}", event.getTaskId());
        rabbitTemplate.convertAndSend(taskExchange, taskCreatedRoutingKey, event);
        log.info("Task created event published for Task ID: {}", event.getTaskId());
    }

    @Retryable(
        retryFor = { AmqpException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishTaskCompletedEvent(TaskCompletedEvent event) {
        log.info("Publishing task completed event for Task ID: {}", event.getTaskId());
        rabbitTemplate.convertAndSend(taskExchange, taskCompletedRoutingKey, event);
        log.info("Task completed event published for Task ID: {}", event.getTaskId());
    }

    @Retryable(
        retryFor = { AmqpException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishResourceDepletedEvent(ResourceDepletedEvent event) {
        log.info("Publishing resource depleted event for Resource: {}", event.getResourceName());
        rabbitTemplate.convertAndSend(taskExchange, resourceDepletedRoutingKey, event);
        log.info("Resource depleted event published for Resource: {}", event.getResourceName());
    }

    @Recover
    public void recover(AmqpException e, Object event) {
        log.error("Failed to publish event after retries: {}", event, e);
    }
}