package com.wolfhack.vetoptim.common.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing details of the Vaccination")
public class VaccinationResponseDTO {

    @Schema(description = "The ID of the vaccination", example = "1")
    private Long id;

    @Schema(description = "The name of the vaccine", example = "Rabies")
    private String vaccineName;

    @Schema(description = "The date of the vaccination", example = "2024-05-15")
    private LocalDate vaccinationDate;

    @Schema(description = "The next due date for the vaccination", example = "2025-05-15")
    private LocalDate nextDueDate;

    @Schema(description = "The ID of the pet", example = "100")
    private Long petId;
}