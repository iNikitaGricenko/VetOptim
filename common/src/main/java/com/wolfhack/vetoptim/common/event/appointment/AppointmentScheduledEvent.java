package com.wolfhack.vetoptim.common.event.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentScheduledEvent {
    private String appointmentId;
    private Long ownerId;
    private Long vetId;
    private LocalDateTime appointmentDate;
    private String type;  // VIDEO_CONSULTATION or IN_PERSON
}
