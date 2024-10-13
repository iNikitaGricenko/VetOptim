package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.FollowUpTaskCreationEvent;
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
class FollowUpTaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private IFollowUpTaskEventPublisher IFollowUpTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(IFollowUpTaskEventPublisher, "taskExchange", "task-exchange");
        ReflectionTestUtils.setField(IFollowUpTaskEventPublisher, "followUpTaskRoutingKey", "task.followup");
    }

    @Test
    void testPublishFollowUpTaskCreationEvent() {
        FollowUpTaskCreationEvent event = new FollowUpTaskCreationEvent(1L, "Buddy", "Follow-up required", "2024-01-01");

        IFollowUpTaskEventPublisher.publishFollowUpTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "task.followup", event);
    }
}