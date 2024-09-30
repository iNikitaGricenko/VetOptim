package com.wolfhack.vetoptim.owner.event;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OwnerEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OwnerEventPublisher ownerEventPublisher;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routingKey.owner.created}")
    private String ownerCreatedRoutingKey;

    @Value("${rabbitmq.routingKey.owner.updated}")
    private String ownerUpdatedRoutingKey;

    @Value("${rabbitmq.routingKey.owner.deleted}")
    private String ownerDeletedRoutingKey;

    @Test
    public void shouldPublishOwnerCreatedEvent() {
        OwnerCreatedEvent event = new OwnerCreatedEvent(1L, "John Doe", "john@example.com");
        ownerEventPublisher.publishOwnerCreatedEvent(event);

        verify(rabbitTemplate).convertAndSend(ownerExchange, ownerCreatedRoutingKey, event);
    }

    @Test
    public void shouldPublishOwnerUpdatedEvent() {
        OwnerUpdatedEvent event = new OwnerUpdatedEvent(1L, "John Doe", "john@example.com");
        ownerEventPublisher.publishOwnerUpdatedEvent(event);

        verify(rabbitTemplate).convertAndSend(ownerExchange, ownerUpdatedRoutingKey, event);
    }

    @Test
    public void shouldPublishOwnerDeletedEvent() {
        OwnerDeletedEvent event = new OwnerDeletedEvent(1L);
        ownerEventPublisher.publishOwnerDeletedEvent(event);

        verify(rabbitTemplate).convertAndSend(ownerExchange, ownerDeletedRoutingKey, event);
    }
}