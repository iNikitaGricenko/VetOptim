package com.wolfhack.vetoptim.taskresource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.notification.urgent}")
    private String urgentTaskTopic;

    @Value("${kafka.topic.notification.resource.depletion}")
    private String resourceDepletionTopic;

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void notifyStaffOfUrgentTask(Long taskId, String description) {
        log.info("Notifying staff: Task {} is URGENT. Description: {}", taskId, description);

        String message = String.format("URGENT Task Alert! Task ID: %d, Description: %s", taskId, description);
        String key = "task-" + taskId;

        try {
            kafkaTemplate.send(urgentTaskTopic, key, message).get(10, TimeUnit.SECONDS);
            log.info("Urgent task notification sent for Task ID: {}", taskId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending urgent task notification for Task ID: {}", taskId, e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to send urgent task notification for Task ID: {}", taskId, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void notifyOfResourceDepletion(String resourceName, int remainingQuantity) {
        log.info("Notifying staff: Resource {} is running low. Remaining quantity: {}", resourceName, remainingQuantity);

        String message = String.format("Resource Depletion Alert! Resource: %s, Remaining Quantity: %d", resourceName, remainingQuantity);
        String key = "resource-" + resourceName;

        try {
            kafkaTemplate.send(resourceDepletionTopic, key, message).get(10, TimeUnit.SECONDS);
            log.info("Resource depletion notification sent for Resource: {}", resourceName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending resource depletion notification for Resource: {}", resourceName, e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to send resource depletion notification for Resource: {}", resourceName, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, Long taskId, String description) {
        log.error("Failed to send urgent task notification for Task ID: {} after retries. Error: {}", taskId, e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, String resourceName, int remainingQuantity) {
        log.error("Failed to send resource depletion notification for Resource: {} after retries. Error: {}", resourceName, e.getMessage());
    }
}
