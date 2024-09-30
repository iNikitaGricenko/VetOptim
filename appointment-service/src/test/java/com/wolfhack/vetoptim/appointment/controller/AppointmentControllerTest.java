package com.wolfhack.vetoptim.appointment.controller;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentControllerTest {

	@Mock
	private AppointmentService appointmentService;

	@InjectMocks
	private AppointmentController appointmentController;

	private MockMvc mockMvc;

	private AutoCloseable openedMocks;

	@BeforeEach
	void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
	}

	@AfterEach
	void tearDown() throws Exception {
        openedMocks.close();
    }

    @Test
    void testCreateAppointment() throws Exception {
        Long ownerId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(appointmentService.createAppointment(eq(ownerId), any())).thenReturn(appointmentDTO);

        mockMvc.perform(post("/appointments/owner/{ownerId}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAppointmentById() throws Exception {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(java.util.Optional.of(appointmentDTO));

        mockMvc.perform(get("/appointments/{id}", appointmentId))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateAppointmentStatus() throws Exception {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(appointmentService.updateAppointmentStatus(eq(appointmentId), eq(AppointmentStatus.CONFIRMED)))
                .thenReturn(appointmentDTO);

        mockMvc.perform(patch("/appointments/{id}/status", appointmentId)
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk());
    }

    @Test
    void testRescheduleAppointment() throws Exception {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(appointmentService.rescheduleAppointment(eq(appointmentId), any())).thenReturn(appointmentDTO);

        mockMvc.perform(patch("/appointments/{id}/reschedule", appointmentId)
                .param("newDate", "2024-12-01T10:00:00"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchAppointments() throws Exception {
        when(appointmentService.searchAppointments(any(), any(), any(), any())).thenReturn(List.of(new AppointmentDTO()));

        mockMvc.perform(get("/appointments/search")
                .param("veterinarianName", "Dr. Smith")
                .param("startDate", "2024-10-01T00:00:00")
                .param("endDate", "2024-10-02T00:00:00")
                .param("status", "SCHEDULED"))
                .andExpect(status().isOk());
    }
}
