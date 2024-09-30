package com.wolfhack.vetoptim.common.dto;

import com.wolfhack.vetoptim.common.AppointmentStatus;
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

    @NotNull
    private Long petId;

    @NotBlank
    private String petName;

    @NotBlank
    private String veterinarianName;

    private String description;

    @NotNull
    private LocalDateTime appointmentDate;

    private Boolean recurring;

    private Integer recurrenceInterval;

    @NotNull
    private AppointmentStatus status;

	private String diagnosis;

	private String treatment;
}