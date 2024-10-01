package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.EmergencyTaskCreationEvent;
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
class EmergencyTaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmergencyTaskEventPublisher emergencyTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emergencyTaskEventPublisher, "taskExchange", "task-exchange");
        ReflectionTestUtils.setField(emergencyTaskEventPublisher, "emergencyTaskRoutingKey", "task.emergency");
    }

    @Test
    void testPublishEmergencyTaskCreationEvent() {
        EmergencyTaskCreationEvent event = new EmergencyTaskCreationEvent(1L, "Buddy", "Emergency condition", "Emergency surgery required");

        emergencyTaskEventPublisher.publishEmergencyTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "task.emergency", event);
    }
}