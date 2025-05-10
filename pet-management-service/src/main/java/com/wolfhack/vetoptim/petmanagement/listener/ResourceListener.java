package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.INotificationService;
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
public class ResourceListener {

    private final INotificationService INotificationService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.resource.depleted}",
        groupId = "${kafka.consumer.group.resource}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleResourceDepletion(
            @Payload ResourceDepletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received resource depletion event from topic {}, key {}: Resource = {}, Remaining Quantity = {}", 
                topic, key, event.getResourceName(), event.getRemainingQuantity());

        INotificationService.notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());

        log.info("Resource depletion handling completed for Resource: {}", event.getResourceName());
    }
}
