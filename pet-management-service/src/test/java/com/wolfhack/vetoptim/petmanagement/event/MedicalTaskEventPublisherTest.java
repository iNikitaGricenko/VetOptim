package com.wolfhack.vetoptim.petmanagement.event;

import com.wolfhack.vetoptim.common.event.task.MedicalTaskCreationEvent;
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
class MedicalTaskEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private MedicalTaskEventPublisher medicalTaskEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(medicalTaskEventPublisher, "medicalTaskTopic", "task-medical");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void testPublishMedicalTaskCreationEvent() {
        MedicalTaskCreationEvent event = new MedicalTaskCreationEvent(1L, "Buddy", "John Doe", "Fever", "Medical task for treatment");
        String key = "pet-" + event.getPetId();

        medicalTaskEventPublisher.publishMedicalTaskCreationEvent(event);

        verify(kafkaTemplate).send("task-medical", key, event);
    }
}
