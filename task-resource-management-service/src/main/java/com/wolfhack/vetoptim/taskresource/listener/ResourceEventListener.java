package com.wolfhack.vetoptim.taskresource.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventListener {

    private final NotificationService notificationService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.resource.depleted}",
        groupId = "${kafka.consumer.group.resource.depleted}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleResourceDepleted(
            @Payload ResourceDepletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received resource depletion event from topic {}, partition {}, key {}: Resource = {}, Remaining Quantity = {}", 
                topic, partition, key, event.getResourceName(), event.getRemainingQuantity());

        notificationService.notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());
        log.info("Resource depletion notification sent for Resource: {}", event.getResourceName());
    }
}
