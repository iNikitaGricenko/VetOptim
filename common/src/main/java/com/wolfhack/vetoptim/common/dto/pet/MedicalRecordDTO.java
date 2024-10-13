package com.wolfhack.vetoptim.common.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for transferring medical record details")
public class MedicalRecordDTO {

    @Schema(description = "The ID of the medical record", example = "1")
    private Long id;

    @Schema(description = "The diagnosis for the pet's condition", example = "Fever")
    @NotBlank(message = "Diagnosis cannot be blank")
    private String diagnosis;

    @Schema(description = "The treatment provided for the pet", example = "Medication")
    @NotBlank(message = "Treatment cannot be blank")
    private String treatment;

    @Schema(description = "The date when the treatment was given", example = "2023-08-14")
    @NotNull(message = "Date of treatment cannot be null")
    private LocalDate dateOfTreatment;

    @Schema(description = "The ID of the pet associated with this medical record", example = "101")
    @NotNull(message = "Pet ID cannot be null")
    private Long petId;
}
