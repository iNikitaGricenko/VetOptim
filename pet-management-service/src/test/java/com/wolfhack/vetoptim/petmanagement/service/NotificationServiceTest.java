package com.wolfhack.vetoptim.petmanagement.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationService notificationService;

	private AutoCloseable autoCloseable;

	@BeforeEach
    void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testNotifyOwnerOfAppointment_Success() {
        AppointmentDTO appointment = new AppointmentDTO();
        appointment.setPetId(1L);
        appointment.setVeterinarianName("Dr. Smith");
        appointment.setAppointmentDate(LocalDateTime.parse("2023-10-10T10:00"));

        notificationService.notifyOwnerOfAppointment(appointment);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }
}