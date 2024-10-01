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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TaskEventPublisher taskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskEventPublisher, "taskExchange", "task-exchange");
        ReflectionTestUtils.setField(taskEventPublisher, "taskCreatedRoutingKey", "task.created");
        ReflectionTestUtils.setField(taskEventPublisher, "taskCompletedRoutingKey", "task.completed");
        ReflectionTestUtils.setField(taskEventPublisher, "resourceDepletedRoutingKey", "resource.depleted");
    }

    @Test
    void publishTaskCreatedEvent_Success() {
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Surgery scheduled");

        taskEventPublisher.publishTaskCreatedEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "task.created", event);
    }

    @Test
    void publishTaskCompletedEvent_Success() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "Surgery", "Surgery completed", TaskStatus.COMPLETED);

        taskEventPublisher.publishTaskCompletedEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "task.completed", event);
    }

    @Test
    void publishResourceDepletedEvent_Success() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        taskEventPublisher.publishResourceDepletedEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "resource.depleted", event);
    }

    @Test
    void testRecover() {
        AmqpException exception = new AmqpException("Failed to send event");
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Surgery scheduled");

        taskEventPublisher.recover(exception, event);

        verifyNoMoreInteractions(rabbitTemplate);
    }
}
