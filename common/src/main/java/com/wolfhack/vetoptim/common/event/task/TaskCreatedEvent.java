package com.wolfhack.vetoptim.common.event.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreatedEvent {
    private Long taskId;
    private Long petId;
    private String taskDescription;
    private String taskType;
}