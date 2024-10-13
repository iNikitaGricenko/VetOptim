package com.wolfhack.vetoptim.common.dto.pet;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for responding with pet interaction details")
public class PetInteractionResponseDTO {
    @Schema(description = "The ID of the interaction", example = "1")
    private Long id;

    @Schema(description = "Type of interaction", example = "Playtime")
    private String interactionType;

    @Schema(description = "Description of the interaction", example = "Played in the yard")
    private String description;

    @Schema(description = "Date and time of the interaction", example = "2024-10-10T14:30")
    private LocalDateTime interactionDate;

    @Schema(description = "The ID of the pet involved in the interaction", example = "1001")
    private Long petId;
}