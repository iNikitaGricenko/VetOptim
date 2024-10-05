package com.wolfhack.vetoptim.common.dto.billing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResourceCostDTO {

    @NotNull(message = "Resource ID cannot be null")
    private Long resourceId;

    @NotBlank(message = "Resource name cannot be blank")
    private String resourceName;

    @Min(value = 0, message = "Cost must be greater than or equal to zero")
    private BigDecimal cost;
}
