package com.wolfhack.vetoptim.common.event.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDeletedEvent {
    private Long appointmentId;
}