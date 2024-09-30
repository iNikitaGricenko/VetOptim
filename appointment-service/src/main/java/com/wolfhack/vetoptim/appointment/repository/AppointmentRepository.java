package com.wolfhack.vetoptim.appointment.repository;

import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPetId(Long petId);

    List<Appointment> findAllByOwnerId(Long ownerId);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Appointment> findByVeterinarianNameAndAppointmentDateBetweenAndStatus(
        String veterinarianName, LocalDateTime startDate, LocalDateTime endDate, AppointmentStatus status
    );

    @Query("SELECT a FROM Appointment a WHERE a.recurring = true AND a.appointmentDate > :afterDate")
    List<Appointment> findRecurringAppointmentsDueAfter(@Param("afterDate") LocalDateTime afterDate);

}