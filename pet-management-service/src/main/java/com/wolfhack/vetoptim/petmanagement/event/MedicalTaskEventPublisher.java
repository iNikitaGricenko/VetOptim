package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
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
public class MedicalTaskEventPublisher implements IMedicalTaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.task.medical}")
    private String medicalTaskTopic;

    @Override
    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishMedicalTaskCreationEvent(MedicalTaskCreationEvent event) {
        log.info("Publishing medical task creation event for Pet ID: {}", event.getPetId());
        String key = "pet-" + event.getPetId();

        try {
            kafkaTemplate.send(medicalTaskTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Medical task creation event published for Pet ID: {}", event.getPetId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing medical task creation event for Pet ID: {}", event.getPetId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish medical task creation event for Pet ID: {}", event.getPetId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, MedicalTaskCreationEvent event) {
        log.error("Failed to publish medical task creation event for Pet ID: {} after retries. Error: {}", 
                event.getPetId(), e.getMessage());
    }
}
