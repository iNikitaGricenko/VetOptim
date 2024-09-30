package com.wolfhack.vetoptim.common.event.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentTaskCreationEvent {
    private Long appointmentId;
    private Long petId;
    private String petName;
    private String veterinarianName;
    private String taskType;
    private String description;
}