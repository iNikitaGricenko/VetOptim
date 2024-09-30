package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.appointment.client.OwnerClient;
import com.wolfhack.vetoptim.appointment.event.AppointmentEventPublisher;
import com.wolfhack.vetoptim.appointment.exception.AppointmentNotFoundException;
import com.wolfhack.vetoptim.appointment.mapper.AppointmentMapper;
import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.appointment.repository.AppointmentRepository;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private OwnerClient ownerClient;

    @Mock
    private AppointmentEventPublisher appointmentEventPublisher;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void testCreateAppointment_success() {
        Long ownerId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAppointmentDate(LocalDateTime.now().plusDays(5));
        Appointment appointment = new Appointment();

        when(ownerClient.ownerExists(ownerId)).thenReturn(true);
        when(appointmentMapper.toEntity(any())).thenReturn(appointment);
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(appointmentMapper.toDTO(any())).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.createAppointment(ownerId, appointmentDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(any());
        verify(notificationService).notifyOwnerOfAppointment(anyString());
    }

    @Test
    void testCreateAppointment_ownerNotFound() {
        Long ownerId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(ownerClient.ownerExists(ownerId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(ownerId, appointmentDTO);
        });
    }

    @Test
    void testUpdateAppointment_success() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAppointmentDate(LocalDateTime.now().plusDays(5));
        Appointment existingAppointment = new Appointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentMapper.toDTO(any())).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, appointmentDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(existingAppointment);
        verify(notificationService).notifyOwnerOfAppointment(anyString());
    }

    @Test
    void testUpdateAppointment_notFound() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.updateAppointment(appointmentId, appointmentDTO);
        });
    }

    @Test
    void testDeleteAppointment_success() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository).deleteById(appointmentId);
        verify(notificationService).notifyOwnerOfAppointment(anyString());
    }

    @Test
    void testUpdateAppointmentStatus_success() {
        Long appointmentId = 1L;
        Appointment existingAppointment = new Appointment();
        AppointmentStatus newStatus = AppointmentStatus.CONFIRMED;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentMapper.toDTO(any())).thenReturn(new AppointmentDTO());

        AppointmentDTO result = appointmentService.updateAppointmentStatus(appointmentId, newStatus);

        assertNotNull(result);
        verify(appointmentRepository).save(existingAppointment);
        assertEquals(newStatus, existingAppointment.getStatus());
    }

    @Test
    void testSearchAppointments_success() {
        String vetName = "Dr. Smith";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        AppointmentStatus status = AppointmentStatus.SCHEDULED;
        Appointment appointment = new Appointment();

        when(appointmentRepository.findByVeterinarianNameAndAppointmentDateBetweenAndStatus(
            vetName, startDate, endDate, status))
            .thenReturn(List.of(appointment));

        List<AppointmentDTO> result = appointmentService.searchAppointments(vetName, startDate, endDate, status);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}