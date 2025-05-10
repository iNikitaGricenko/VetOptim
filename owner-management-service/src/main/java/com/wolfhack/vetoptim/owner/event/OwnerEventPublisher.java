package com.wolfhack.vetoptim.owner.event;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
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
public class OwnerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.owner.created}")
    private String ownerCreatedTopic;

    @Value("${kafka.topic.owner.updated}")
    private String ownerUpdatedTopic;

    @Value("${kafka.topic.owner.deleted}")
    private String ownerDeletedTopic;

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishOwnerCreatedEvent(OwnerCreatedEvent event) {
        log.info("Publishing owner created event for owner ID: {}", event.getOwnerId());
        String key = "owner-" + event.getOwnerId();

        try {
            kafkaTemplate.send(ownerCreatedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Owner created event published for owner ID: {}", event.getOwnerId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing owner created event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish owner created event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishOwnerUpdatedEvent(OwnerUpdatedEvent event) {
        log.info("Publishing owner updated event for owner ID: {}", event.getOwnerId());
        String key = "owner-" + event.getOwnerId();

        try {
            kafkaTemplate.send(ownerUpdatedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Owner updated event published for owner ID: {}", event.getOwnerId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing owner updated event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish owner updated event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishOwnerDeletedEvent(OwnerDeletedEvent event) {
        log.info("Publishing owner deleted event for owner ID: {}", event.getOwnerId());
        String key = "owner-" + event.getOwnerId();

        try {
            kafkaTemplate.send(ownerDeletedTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Owner deleted event published for owner ID: {}", event.getOwnerId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing owner deleted event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish owner deleted event for owner ID: {}", event.getOwnerId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, OwnerCreatedEvent event) {
        log.error("Failed to publish owner created event for owner ID: {} after retries. Error: {}", 
                event.getOwnerId(), e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, OwnerUpdatedEvent event) {
        log.error("Failed to publish owner updated event for owner ID: {} after retries. Error: {}", 
                event.getOwnerId(), e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, OwnerDeletedEvent event) {
        log.error("Failed to publish owner deleted event for owner ID: {} after retries. Error: {}", 
                event.getOwnerId(), e.getMessage());
    }
}
