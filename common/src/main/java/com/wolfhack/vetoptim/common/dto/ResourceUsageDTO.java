package com.wolfhack.vetoptim.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceUsageDTO {
    private Long resourceId;
    private String resourceName;
    private int quantityUsed;
}