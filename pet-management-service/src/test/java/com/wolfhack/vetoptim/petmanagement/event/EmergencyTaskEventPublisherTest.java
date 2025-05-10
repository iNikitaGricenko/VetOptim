package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
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
class EmergencyTaskEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EmergencyTaskEventPublisher emergencyTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emergencyTaskEventPublisher, "emergencyTaskTopic", "task-emergency");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishEmergencyTaskCreationEvent() {
        EmergencyTaskCreationEvent event = new EmergencyTaskCreationEvent(1L, "Buddy", "Emergency condition", "Emergency surgery required");
        String key = "pet-" + event.getPetId();

        emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(event);

        verify(kafkaTemplate).send("task-emergency", key, event);
    }
}
