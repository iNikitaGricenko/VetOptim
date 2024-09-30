package com.wolfhack.vetoptim.common.event.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdatedEvent {
    private Long appointmentId;
    private Long petId;
    private Long ownerId;
    private String veterinarianName;
    private LocalDateTime newAppointmentDate;
}