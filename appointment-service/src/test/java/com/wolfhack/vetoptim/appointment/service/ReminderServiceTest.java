package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ReminderService reminderService;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderService(appointmentService, kafkaTemplate);
        ReflectionTestUtils.setField(reminderService, "reminderTopic", "reminder-topic");

        // Mock the CompletableFuture returned by kafkaTemplate.send()
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);
    }

    @Test
    void sendAppointmentReminders_Success() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusDays(1);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(1L);
        appointmentDTO.setPetId(2L);
        appointmentDTO.setPetName("Buddy");
        appointmentDTO.setVeterinarianName("Dr. Smith");
        appointmentDTO.setAppointmentDate(reminderThreshold);

        when(appointmentService.getAppointmentsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(List.of(appointmentDTO));

        reminderService.sendAppointmentReminders();

        verify(appointmentService).getAppointmentsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(kafkaTemplate).send(anyString(), anyString(), anyString());
    }



}
