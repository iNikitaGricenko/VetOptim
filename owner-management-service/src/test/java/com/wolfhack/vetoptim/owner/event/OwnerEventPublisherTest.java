package com.wolfhack.vetoptim.owner.event;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerDeletedEvent;
import com.wolfhack.vetoptim.common.event.owner.OwnerUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OwnerEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OwnerEventPublisher ownerEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerExchange", "owner-exchange");
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerCreatedRoutingKey", "owner.created");
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerUpdatedRoutingKey", "owner.updated");
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerDeletedRoutingKey", "owner.deleted");
    }

    @Test
    void testPublishOwnerCreatedEvent() {
        OwnerCreatedEvent event = new OwnerCreatedEvent(1L, "John Doe", "Contact");

        ownerEventPublisher.publishOwnerCreatedEvent(event);

        verify(rabbitTemplate).convertAndSend("owner-exchange", "owner.created", event);
    }

    @Test
    void testPublishOwnerUpdatedEvent() {
        OwnerUpdatedEvent event = new OwnerUpdatedEvent(1L, "John Doe", "Contact");

        ownerEventPublisher.publishOwnerUpdatedEvent(event);

        verify(rabbitTemplate).convertAndSend("owner-exchange", "owner.updated", event);
    }

    @Test
    void testPublishOwnerDeletedEvent() {
        OwnerDeletedEvent event = new OwnerDeletedEvent(1L);

        ownerEventPublisher.publishOwnerDeletedEvent(event);

        verify(rabbitTemplate).convertAndSend("owner-exchange", "owner.deleted", event);
    }
}