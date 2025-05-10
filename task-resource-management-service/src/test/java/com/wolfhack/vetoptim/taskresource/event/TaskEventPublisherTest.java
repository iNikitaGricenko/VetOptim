package com.wolfhack.vetoptim.taskresource.event;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.common.event.task.TaskCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TaskEventPublisher taskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskEventPublisher, "taskCreatedTopic", "task-created");
        ReflectionTestUtils.setField(taskEventPublisher, "taskCompletedTopic", "task-completed");
        ReflectionTestUtils.setField(taskEventPublisher, "resourceDepletedTopic", "resource-depleted");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        lenient().when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void publishTaskCreatedEvent_Success() {
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Surgery scheduled");
        String key = "task-" + event.getTaskId();

        taskEventPublisher.publishTaskCreatedEvent(event);

        verify(kafkaTemplate).send("task-created", key, event);
    }

    @Test
    void publishTaskCompletedEvent_Success() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "Surgery", "Surgery completed", TaskStatus.COMPLETED);
        String key = "task-" + event.getTaskId();

        taskEventPublisher.publishTaskCompletedEvent(event);

        verify(kafkaTemplate).send("task-completed", key, event);
    }

    @Test
    void publishResourceDepletedEvent_Success() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);
        String key = "resource-" + event.getResourceName();

        taskEventPublisher.publishResourceDepletedEvent(event);

        verify(kafkaTemplate).send("resource-depleted", key, event);
    }

    @Test
    void testRecoverTaskCreated() {
        RuntimeException exception = new RuntimeException("Failed to send event");
        TaskCreatedEvent event = new TaskCreatedEvent(1L, 101L, "Surgery", "Surgery scheduled");

        taskEventPublisher.recover(exception, event);

        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void testRecoverTaskCompleted() {
        RuntimeException exception = new RuntimeException("Failed to send event");
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "Surgery", "Surgery completed", TaskStatus.COMPLETED);

        taskEventPublisher.recover(exception, event);

        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void testRecoverResourceDepleted() {
        RuntimeException exception = new RuntimeException("Failed to send event");
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        taskEventPublisher.recover(exception, event);

        verifyNoMoreInteractions(kafkaTemplate);
    }
}
