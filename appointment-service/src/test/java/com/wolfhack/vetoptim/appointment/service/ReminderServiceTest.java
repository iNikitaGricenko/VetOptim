package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class ReminderServiceTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReminderService reminderService;

	private AutoCloseable openedMocks;

	@BeforeEach
    void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
    }

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testSendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetName("Fluffy");
        appointmentDTO.setAppointmentDate(now.plusDays(1));

        when(appointmentService.getAppointmentsForDateRange(any(), any())).thenReturn(List.of(appointmentDTO));

        reminderService.sendAppointmentReminders();

        verify(appointmentService).getAppointmentsForDateRange(any(), any());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }
}