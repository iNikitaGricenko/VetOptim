package com.wolfhack.vetoptim.appointment.scheduler;

import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringAppointmentSchedulerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private RecurringAppointmentScheduler recurringAppointmentScheduler;

    @Test
    void scheduleNewRecurringAppointments_Success() {
        LocalDateTime now = LocalDateTime.now();
        Appointment appointment = new Appointment();
        appointment.setPetId(1L);
        appointment.setPetName("Buddy");
        appointment.setVeterinarianName("Dr. Smith");
        appointment.setDescription("Checkup");
        appointment.setAppointmentDate(now.minusDays(1));
        appointment.setOwnerId(100L);
        appointment.setOwnerName("John Doe");
        appointment.setRecurring(true);
        appointment.setRecurrenceInterval(30);

        when(appointmentRepository.findRecurringAppointmentsDueAfter(any(LocalDateTime.class)))
            .thenReturn(List.of(appointment));

        recurringAppointmentScheduler.scheduleNewRecurringAppointments();

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(appointmentRepository).save(argThat(savedAppointment ->
            savedAppointment.getPetId().equals(1L) &&
            savedAppointment.getAppointmentDate().equals(now.plusDays(29))
        ));
    }

    @Test
    void scheduleNewRecurringAppointments_NoAppointments() {
        when(appointmentRepository.findRecurringAppointmentsDueAfter(any(LocalDateTime.class)))
            .thenReturn(List.of());

        recurringAppointmentScheduler.scheduleNewRecurringAppointments();

        verify(appointmentRepository, times(0)).save(any(Appointment.class));
    }
}
