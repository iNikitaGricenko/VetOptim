package com.wolfhack.vetoptim.appointment.controller;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void getAppointmentsForPet_Success() {
        Long petId = 1L;
        List<AppointmentDTO> appointmentDTOList = List.of(new AppointmentDTO());
        when(appointmentService.getAppointmentsForPet(petId)).thenReturn(appointmentDTOList);

        ResponseEntity<List<AppointmentDTO>> response = appointmentController.getAppointmentsForPet(petId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTOList, response.getBody());
        verify(appointmentService, times(1)).getAppointmentsForPet(petId);
    }

    @Test
    void getAppointmentById_Success() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(appointmentDTO));

        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById(appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
        verify(appointmentService, times(1)).getAppointmentById(appointmentId);
    }

    @Test
    void createAppointment_Success() {
        Long ownerId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        when(appointmentService.createAppointment(ownerId, appointmentDTO)).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> response = appointmentController.createAppointment(ownerId, appointmentDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
        verify(appointmentService, times(1)).createAppointment(ownerId, appointmentDTO);
    }

    @Test
    void updateAppointment_Success() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        when(appointmentService.updateAppointment(appointmentId, appointmentDTO)).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> response = appointmentController.updateAppointment(appointmentId, appointmentDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
        verify(appointmentService, times(1)).updateAppointment(appointmentId, appointmentDTO);
    }

    @Test
    void updateOwnerInfoForAppointments_Success() {
        Long ownerId = 1L;
        OwnerDTO ownerDTO = new OwnerDTO();

        ResponseEntity<Void> response = appointmentController.updateOwnerInfoForAppointments(ownerId, ownerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(appointmentService, times(1)).updateOwnerInfoForAppointments(ownerId, ownerDTO);
    }

    @Test
    void deleteAppointment_Success() {
        Long appointmentId = 1L;

        ResponseEntity<Void> response = appointmentController.deleteAppointment(appointmentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService, times(1)).deleteAppointment(appointmentId);
    }
}
