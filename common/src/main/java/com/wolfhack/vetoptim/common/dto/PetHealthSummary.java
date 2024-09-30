package com.wolfhack.vetoptim.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHealthSummary {
    private int numberOfVisits;
    private String latestCondition;
    private String healthTrend;

    public PetHealthSummary(String message) {
        this.latestCondition = message;
    }
}