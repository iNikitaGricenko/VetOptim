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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OwnerEventPublisher ownerEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerCreatedTopic", "owner-created");
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerUpdatedTopic", "owner-updated");
        ReflectionTestUtils.setField(ownerEventPublisher, "ownerDeletedTopic", "owner-deleted");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
    }

    @Test
    void testPublishOwnerCreatedEvent() {
        OwnerCreatedEvent event = new OwnerCreatedEvent(1L, "John Doe", "Contact");
        String key = "owner-" + event.getOwnerId();

        ownerEventPublisher.publishOwnerCreatedEvent(event);

        verify(kafkaTemplate).send("owner-created", key, event);
    }

    @Test
    void testPublishOwnerUpdatedEvent() {
        OwnerUpdatedEvent event = new OwnerUpdatedEvent(1L, "John Doe", "Contact");
        String key = "owner-" + event.getOwnerId();

        ownerEventPublisher.publishOwnerUpdatedEvent(event);

        verify(kafkaTemplate).send("owner-updated", key, event);
    }

    @Test
    void testPublishOwnerDeletedEvent() {
        OwnerDeletedEvent event = new OwnerDeletedEvent(1L);
        String key = "owner-" + event.getOwnerId();

        ownerEventPublisher.publishOwnerDeletedEvent(event);

        verify(kafkaTemplate).send("owner-deleted", key, event);
    }
}
