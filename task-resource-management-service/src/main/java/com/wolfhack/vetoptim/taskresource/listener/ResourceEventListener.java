package com.wolfhack.vetoptim.taskresource.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventListener {

    private final NotificationService notificationService;

    @Async
    @RabbitListener(queues = "${rabbitmq.queue.resource.depleted}")
    public void handleResourceDepleted(ResourceDepletedEvent event) {
        log.info("Received resource depletion event: Resource = {}, Remaining Quantity = {}", event.getResourceName(), event.getRemainingQuantity());

        notificationService.notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());
        log.info("Resource depletion notification sent for Resource: {}", event.getResourceName());
    }
}