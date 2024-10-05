package com.wolfhack.vetoptim.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceUsageDTO {
    @NotNull(message = "Resource ID cannot be null")
    private Long resourceId;

    @NotNull(message = "Resource name cannot be null")
    @Size(min = 1, max = 255, message = "Resource name must be between 1 and 255 characters")
    private String resourceName;

    @Min(value = 1, message = "Quantity used must be at least 1")
    private int quantityUsed;
}