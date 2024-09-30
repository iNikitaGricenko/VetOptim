package com.wolfhack.vetoptim.common.event.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedEvent {
    private Long taskId;
    private Long petId;
    private String taskDescription;
    private String assignedStaff;
}