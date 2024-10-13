package com.wolfhack.vetoptim.common.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for creating or updating a Vaccination")
public class VaccinationRequestDTO {

    @Schema(description = "The name of the vaccine", example = "Rabies", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vaccine name cannot be blank")
    private String vaccineName;

    @Schema(description = "The date of the vaccination", example = "2023-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Vaccination date cannot be null")
    @PastOrPresent(message = "Vaccination date must be in the past or present")
    private LocalDate vaccinationDate;

    @Schema(description = "The next due date for the vaccination", example = "2024-05-15", required = true)
    @NotNull(message = "Next due date is mandatory")
    @FutureOrPresent(message = "Next due date must be in the future or present")
    private LocalDate nextDueDate;
}