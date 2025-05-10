package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpTaskEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private FollowUpTaskEventPublisher followUpTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(followUpTaskEventPublisher, "followUpTaskTopic", "task-followup");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishFollowUpTaskCreationEvent() {
        FollowUpTaskCreationEvent event = new FollowUpTaskCreationEvent(1L, "Buddy", "Follow-up required", "2024-01-01");
        String key = "pet-" + event.getPetId();

        followUpTaskEventPublisher.publishFollowUpTaskCreationEvent(event);

        verify(kafkaTemplate).send("task-followup", key, event);
    }
}
