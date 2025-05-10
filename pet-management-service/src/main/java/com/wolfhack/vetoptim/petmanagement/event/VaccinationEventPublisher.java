package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.vaccination.VaccinationReminderEvent;
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
public class VaccinationEventPublisher implements IVaccinationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.vaccination.reminder}")
    private String vaccinationReminderTopic;

    @Override
    @Retryable(
        retryFor = {ExecutionException.class, TimeoutException.class, InterruptedException.class},
        backoff = @Backoff(delay = 2000)
    )
    public void publishVaccinationReminderEvent(VaccinationReminderEvent event) {
        log.info("Publishing vaccination reminder event for Pet ID: {}", event.getPetId());
        String key = "pet-" + event.getPetId();

        try {
            kafkaTemplate.send(vaccinationReminderTopic, key, event).get(10, TimeUnit.SECONDS);
            log.info("Vaccination reminder event published for Pet ID: {}", event.getPetId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing vaccination reminder event for Pet ID: {}", event.getPetId(), e);
            throw new RuntimeException("Message sending interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("Failed to publish vaccination reminder event for Pet ID: {}", event.getPetId(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Recover
    public void recover(RuntimeException e, VaccinationReminderEvent event) {
        log.error("Failed to publish vaccination reminder event for Pet ID: {} after retries. Error: {}", 
                event.getPetId(), e.getMessage());
    }
}
