package com.wolfhack.vetoptim.common.dto.pet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetInteractionResponseDTO {
    private Long id;
    private String interactionType;
    private String description;
    private LocalDateTime interactionDate;
    private Long petId;
}