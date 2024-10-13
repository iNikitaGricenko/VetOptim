package com.wolfhack.vetoptim.common.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for creating a pet interaction request")
public class PetInteractionRequestDTO {

    @Schema(description = "Type of interaction with the pet", example = "Playtime")
    @NotBlank(message = "Interaction type is required")
    private String interactionType;

    @Schema(description = "Description of the interaction", example = "Played in the yard")
    private String description;

    @Schema(description = "Date and time of the interaction", example = "2024-10-10T14:30")
    @NotNull(message = "Interaction date is required")
    private LocalDateTime interactionDate;

}