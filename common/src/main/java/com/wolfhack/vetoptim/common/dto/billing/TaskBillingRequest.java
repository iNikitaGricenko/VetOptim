package com.wolfhack.vetoptim.common.dto.billing;

import com.wolfhack.vetoptim.common.TaskType;
import com.wolfhack.vetoptim.common.dto.ResourceUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskBillingRequest {
    private Long taskId;
    private Long petId;
    private String taskDescription;
    private TaskType taskType;
    private List<ResourceUsageDTO> resourcesUsed;
}