package com.wolfhack.vetoptim.taskresource.service.integration;

import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class NotificationServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationService notificationService;

    @Test
    void testNotifyStaffOfUrgentTask_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        notificationService.notifyStaffOfUrgentTask(1L, "Urgent task description");

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyStaffOfUrgentTask_Recovery() {
        doThrow(new AmqpException("RabbitMQ error")).when(rabbitTemplate)
            .convertAndSend(anyString(), anyString(), anyString());

        notificationService.notifyStaffOfUrgentTask(1L, "Test task description");

        verify(rabbitTemplate, times(3)).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyOfResourceDepletion_Success() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        notificationService.notifyOfResourceDepletion("Surgical Kit", 1);

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void testNotifyOfResourceDepletion_Recovery() {
        doThrow(new AmqpException("RabbitMQ error")).when(rabbitTemplate)
            .convertAndSend(anyString(), anyString(), anyString());

        notificationService.notifyOfResourceDepletion("Surgical Kit", 1);

        verify(rabbitTemplate, times(3)).convertAndSend(anyString(), anyString(), anyString());
    }
}