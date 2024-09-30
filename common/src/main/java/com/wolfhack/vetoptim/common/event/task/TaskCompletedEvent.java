package com.wolfhack.vetoptim.common.event.task;

import com.wolfhack.vetoptim.common.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCompletedEvent {
    private Long taskId;
    private Long petId;
    private String taskType;
    private String description;
    private TaskStatus status;
}