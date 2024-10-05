package com.wolfhack.vetoptim.common.dto;

import com.wolfhack.vetoptim.common.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Pet ID cannot be null")
    private Long petId;

    @NotBlank(message = "Pet name cannot be blank")
    private String petName;

    @NotBlank(message = "Veterinarian name cannot be blank")
    private String veterinarianName;

    private String description;

    @NotNull(message = "Appointment date cannot be null")
    @FutureOrPresent(message = "Appointment date must be in the present or future")
    private LocalDateTime appointmentDate;

    private Boolean recurring;

    private Integer recurrenceInterval;

    @NotNull(message = "Appointment status cannot be null")
    private AppointmentStatus status;

    private String diagnosis;

    private String treatment;
}
