package com.wolfhack.vetoptim.common.dto;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long petId;
    private TaskType taskType;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status;
}
