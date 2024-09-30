package com.wolfhack.vetoptim.taskresource.event;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@EnableRetry
@ExtendWith(MockitoExtension.class)
class TaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TaskEventPublisher taskEventPublisher;

    @Test
    public void testPublishTaskCreatedEvent_Successful() {
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Task description");

        taskEventPublisher.publishTaskCreatedEvent(event);

        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class), any(TaskCreatedEvent.class));
    }

    @Test
    public void testPublishTaskCreatedEvent_RetryOnFailure() {
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Task description");

        doThrow(new AmqpException("Connection failure")).when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(TaskCreatedEvent.class));

        try {
            taskEventPublisher.publishTaskCreatedEvent(event);
        } catch (AmqpException e) {
        }

        verify(rabbitTemplate, times(3)).convertAndSend(any(String.class), any(String.class), any(TaskCreatedEvent.class));
    }

    @Test
    public void testPublishTaskCompletedEvent_Successful() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "Surgery", "Task completed", TaskStatus.COMPLETED);

        taskEventPublisher.publishTaskCompletedEvent(event);

        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class), any(TaskCompletedEvent.class));
    }

    @Test
    public void testPublishTaskCompletedEvent_RetryOnFailure() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "Surgery", "Task completed", TaskStatus.COMPLETED);

        doThrow(new AmqpException("Connection failure")).when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(TaskCompletedEvent.class));

        try {
            taskEventPublisher.publishTaskCompletedEvent(event);
        } catch (AmqpException e) {
        }

        verify(rabbitTemplate, times(3)).convertAndSend(any(String.class), any(String.class), any(TaskCompletedEvent.class));
    }

    @Test
    public void testPublishResourceDepletedEvent_Successful() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        taskEventPublisher.publishResourceDepletedEvent(event);

        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class), any(ResourceDepletedEvent.class));
    }

    @Test
    public void testPublishResourceDepletedEvent_RetryOnFailure() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        doThrow(new AmqpException("Connection failure")).when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(ResourceDepletedEvent.class));

        try {
            taskEventPublisher.publishResourceDepletedEvent(event);
        } catch (AmqpException e) {
        }

        verify(rabbitTemplate, times(3)).convertAndSend(any(String.class), any(String.class), any(ResourceDepletedEvent.class));
    }
}