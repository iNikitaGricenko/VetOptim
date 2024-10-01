package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReminderService reminderService;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderService(appointmentService, rabbitTemplate);
        ReflectionTestUtils.setField(reminderService, "notificationExchange", "notification-exchange");
        ReflectionTestUtils.setField(reminderService, "reminderRoutingKey", "reminder-routing-key");
    }

    @Test
    void sendAppointmentReminders_Success() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusDays(1);

        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setVeterinarianName("Vet");
        appointmentDTO.setAppointmentDate(reminderThreshold);

        when(appointmentService.getAppointmentsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(List.of(appointmentDTO));

        reminderService.sendAppointmentReminders();

        verify(appointmentService).getAppointmentsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }



}
