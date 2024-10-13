package com.wolfhack.vetoptim.common.dto.pet;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationRequestDTO {

    @NotBlank(message = "Vaccine name cannot be blank")
    private String vaccineName;

    @NotNull(message = "Vaccination date cannot be null")
    private LocalDate vaccinationDate;

    @NotNull(message = "Next due date cannot be null")
    @Future(message = "Next due date must be a future date")
    private LocalDate nextDueDate;
}