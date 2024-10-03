package com.wolfhack.vetoptim.common.dto.billing;

import com.wolfhack.vetoptim.common.TaskType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskCostDTO {

    @NotNull(message = "Task type cannot be null")
    private TaskType taskType;

    @Min(value = 0, message = "Cost must be greater than or equal to zero")
    private BigDecimal cost;
}