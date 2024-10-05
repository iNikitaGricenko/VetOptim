package com.wolfhack.vetoptim.appointment.controller;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForPet(@PathVariable("petId") Long petId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForPet(petId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.of(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentDTO>> searchAppointments(
        @RequestParam(name = "veterinarianName", required = false) String veterinarianName,
        @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
        @RequestParam(name = "endDate", required = false) LocalDateTime endDate,
        @RequestParam(name = "status", required = false) AppointmentStatus status
    ) {
        return ResponseEntity.ok(appointmentService.searchAppointments(veterinarianName, startDate, endDate, status));
    }

    @PostMapping("/owner/{ownerId}")
    public ResponseEntity<AppointmentDTO> createAppointment(@PathVariable("ownerId") Long ownerId, @Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO appointment = appointmentService.createAppointment(ownerId, appointmentDTO);
        return ResponseEntity.created(
		        URI.create("/api/appointments/"+appointment.getId())
            )
            .body(appointment);
    }

    @PatchMapping("/owner/{ownerId}/update-owner-info")
    public ResponseEntity<Void> updateOwnerInfoForAppointments(@PathVariable("ownerId") Long ownerId, @RequestBody OwnerDTO ownerDTO) {
        appointmentService.updateOwnerInfoForAppointments(ownerId, ownerDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable("id") Long id, @Valid @RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, appointmentDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(@PathVariable("id") Long id, @RequestParam("status") AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(@PathVariable("id") Long id, @RequestParam("newDate") LocalDateTime newDate) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, newDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}