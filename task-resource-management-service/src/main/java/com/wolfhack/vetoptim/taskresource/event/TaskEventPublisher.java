package com.wolfhack.vetoptim.taskresource.event;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.task.created}")
    private String taskCreatedTopic;

    @Value("${kafka.topic.task.completed}")
    private String taskCompletedTopic;

    @Value("${kafka.topic.resource.depleted}")
    private String resourceDepletedTopic;

    @Retryable(
        retryFor = { ExecutionException.class, TimeoutException.class, InterruptedException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishTaskCreatedEvent(TaskCreatedEvent event) {
        log.info("Publishing task created event for Task ID: {}", event.getTaskId());
        String key = "task-" + event.getTaskId();

        try {
            kafkaTemplate.send(taskCreatedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Task created event published for Task ID: {}", event.getTaskId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing task created event for Task ID: {}", event.getTaskId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish task created event for Task ID: {}", event.getTaskId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = { ExecutionException.class, TimeoutException.class, InterruptedException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishTaskCompletedEvent(TaskCompletedEvent event) {
        log.info("Publishing task completed event for Task ID: {}", event.getTaskId());
        String key = "task-" + event.getTaskId();

        try {
            kafkaTemplate.send(taskCompletedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Task completed event published for Task ID: {}", event.getTaskId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing task completed event for Task ID: {}", event.getTaskId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish task completed event for Task ID: {}", event.getTaskId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = { ExecutionException.class, TimeoutException.class, InterruptedException.class },
        backoff = @Backoff(delay = 2000)
    )
    public void publishResourceDepletedEvent(ResourceDepletedEvent event) {
        log.info("Publishing resource depleted event for Resource: {}", event.getResourceName());
        String key = "resource-" + event.getResourceName();

        try {
            kafkaTemplate.send(resourceDepletedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Resource depleted event published for Resource: {}", event.getResourceName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing resource depleted event for Resource: {}", event.getResourceName(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish resource depleted event for Resource: {}", event.getResourceName(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, TaskCreatedEvent event) {
        log.error("Failed to publish task created event for Task ID: {} after retries. Error: {}", event.getTaskId(), e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, TaskCompletedEvent event) {
        log.error("Failed to publish task completed event for Task ID: {} after retries. Error: {}", event.getTaskId(), e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, ResourceDepletedEvent event) {
        log.error("Failed to publish resource depleted event for Resource: {} after retries. Error: {}", event.getResourceName(), e.getMessage());
    }
}
