package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceListener {

    private final NotificationService notificationService;

    @Async
    @RabbitListener(queues = "${rabbitmq.queue.resource.depleted}")
    public void handleResourceDepletion(ResourceDepletedEvent event) {
        log.info("Received ResourceDepletedEvent for Resource: {} with remaining quantity: {}", event.getResourceName(), event.getRemainingQuantity());

        notificationService.notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());

        log.info("Resource depletion handling completed for Resource: {}", event.getResourceName());
    }
}