package com.wolfhack.vetoptim.common.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDTO {

    private Long id;

    @NotBlank(message = "Diagnosis cannot be blank")
    private String diagnosis;

    @NotBlank(message = "Treatment cannot be blank")
    private String treatment;

    @NotNull(message = "Date of treatment cannot be null")
    private LocalDate dateOfTreatment;

    @NotNull(message = "Pet ID cannot be null")
    private Long petId;
}
