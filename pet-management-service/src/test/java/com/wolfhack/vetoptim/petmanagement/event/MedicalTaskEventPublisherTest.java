package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
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
class MedicalTaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MedicalTaskEventPublisher medicalTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(medicalTaskEventPublisher, "taskExchange", "task-exchange");
        ReflectionTestUtils.setField(medicalTaskEventPublisher, "taskRoutingKey", "task.medical");
    }

    @Test
    void testPublishMedicalTaskCreationEvent() {
        MedicalTaskCreationEvent event = new MedicalTaskCreationEvent(1L, "Buddy", "John Doe", "Fever", "Medical task for treatment");

        medicalTaskEventPublisher.publishMedicalTaskCreationEvent(event);

        verify(rabbitTemplate).convertAndSend("task-exchange", "task.medical", event);
    }
}