package com.wolfhack.vetoptim.appointment.scheduler;

import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringAppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleNewRecurringAppointments() {
        log.info("Checking for new recurring appointments...");

        LocalDateTime now = LocalDateTime.now();
        List<Appointment> recurringAppointments = appointmentRepository.findRecurringAppointmentsDueAfter(now);

        for (Appointment appointment : recurringAppointments) {
            LocalDateTime nextAppointmentDate = appointment.getAppointmentDate().plusDays(appointment.getRecurrenceInterval());

            Appointment newAppointment = new Appointment();
            newAppointment.setPetId(appointment.getPetId());
            newAppointment.setPetName(appointment.getPetName());
            newAppointment.setVeterinarianName(appointment.getVeterinarianName());
            newAppointment.setDescription(appointment.getDescription());
            newAppointment.setAppointmentDate(nextAppointmentDate);
            newAppointment.setOwnerId(appointment.getOwnerId());
            newAppointment.setOwnerName(appointment.getOwnerName());
            newAppointment.setRecurring(true);
            newAppointment.setRecurrenceInterval(appointment.getRecurrenceInterval());

            appointmentRepository.save(newAppointment);
            log.info("New recurring appointment scheduled for Pet ID: {} on {}", appointment.getPetId(), nextAppointmentDate);
        }
    }
}