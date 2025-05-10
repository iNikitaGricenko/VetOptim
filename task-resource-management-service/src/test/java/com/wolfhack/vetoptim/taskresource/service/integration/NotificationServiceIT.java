package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.taskresource.config.DockerComposeTestConfiguration;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
@Import(DockerComposeTestConfiguration.class)
class NotificationServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationService notificationService;

    @Test
    void testNotifyStaffOfUrgentTask_Success() {
        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationService.notifyStaffOfUrgentTask(1L, "Urgent task description");

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyStaffOfUrgentTask_Recovery() {
        // Create a CompletableFuture that completes exceptionally
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka error", new RuntimeException()));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationService.notifyStaffOfUrgentTask(1L, "Test task description");

        // Verify that send was called 3 times (original + 2 retries)
        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyOfResourceDepletion_Success() {
        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationService.notifyOfResourceDepletion("Surgical Kit", 1);

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyOfResourceDepletion_Recovery() {
        // Create a CompletableFuture that completes exceptionally
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new ExecutionException("Kafka error", new RuntimeException()));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationService.notifyOfResourceDepletion("Surgical Kit", 1);

        // Verify that send was called 3 times (original + 2 retries)
        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), anyString());
    }
}
