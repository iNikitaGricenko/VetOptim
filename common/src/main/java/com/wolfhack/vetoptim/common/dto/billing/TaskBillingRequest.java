package com.wolfhack.vetoptim.common.dto.billing;

import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.ResourceUsageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskBillingRequest {
    @NotNull(message = "Task ID cannot be null")
    private Long taskId;

    @NotNull(message = "Pet ID cannot be null")
    private Long petId;

    @NotNull(message = "Task description cannot be null")
    @Size(min = 1, max = 500, message = "Task description must be between 1 and 500 characters")
    private String taskDescription;

    @NotNull(message = "Task type cannot be null")
    private TaskType taskType;

    @NotNull(message = "Resources used cannot be null")
    @Size(min = 1, message = "At least one resource must be used")
    private List<@Valid ResourceUsageDTO> resourcesUsed;
}