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

import com.wolfhack.vetoptim.appointment.client.OwnerClient;
import com.wolfhack.vetoptim.appointment.event.AppointmentEventPublisher;
import com.wolfhack.vetoptim.appointment.mapper.AppointmentMapper;
import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.appointment.repository.AppointmentRepository;
import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.appointment.service.NotificationService;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void getAppointmentsForPet_Success() {
        Long petId = 1L;
        Appointment appointment = new Appointment();
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        when(appointmentRepository.findAllByPetId(petId)).thenReturn(List.of(appointment));
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        List<AppointmentDTO> result = appointmentService.getAppointmentsForPet(petId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAllByPetId(petId);
    }

    @Test
    void createAppointment_Success() {
        Long ownerId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now());

        when(ownerClient.ownerExists(ownerId)).thenReturn(true);
        when(appointmentMapper.toEntity(appointmentDTO)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toDTO(appointment)).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.createAppointment(ownerId, appointmentDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(appointment);
        verify(appointmentEventPublisher).publishAppointmentTaskCreationEvent(any(AppointmentTaskCreationEvent.class));
        verify(notificationService).notifyOwnerOfAppointment(anyString());
    }


    @Test
    void updateAppointment_Success() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        Appointment existingAppointment = new Appointment();
        existingAppointment.setPetName("Buddy");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);
        when(appointmentMapper.toDTO(existingAppointment)).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, appointmentDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(existingAppointment);
        verify(notificationService).notifyOwnerOfAppointment(anyString());
    }

    @Test
    void updateAppointment_NotFound() {
        Long appointmentId = 1L;
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.updateAppointment(appointmentId, appointmentDTO));
    }
}
