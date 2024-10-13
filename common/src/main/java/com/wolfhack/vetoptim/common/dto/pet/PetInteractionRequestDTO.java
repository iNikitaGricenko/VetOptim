package com.wolfhack.vetoptim.common.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetInteractionRequestDTO {

    @NotBlank(message = "Interaction type is required")
    private String interactionType;

    private String description;

    @NotNull(message = "Interaction date is required")
    private LocalDateTime interactionDate;

}