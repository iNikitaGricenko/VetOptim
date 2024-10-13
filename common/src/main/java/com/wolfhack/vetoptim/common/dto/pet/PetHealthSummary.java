package com.wolfhack.vetoptim.common.dto.pet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary of the pet's health, including number of visits, latest condition, and overall health trend")
public class PetHealthSummary {

    @Schema(description = "The total number of visits the pet has had", example = "5")
    private int numberOfVisits;

    @Schema(description = "The latest medical condition of the pet", example = "Healthy")
    private String latestCondition;

    @Schema(description = "The overall health trend of the pet, indicating recurring conditions", example = "No recurring conditions")
    private String healthTrend;

    public PetHealthSummary(String message) {
        this.latestCondition = message;
    }
}