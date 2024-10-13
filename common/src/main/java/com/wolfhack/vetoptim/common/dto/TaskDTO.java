package com.wolfhack.vetoptim.common.dto;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing the details of a task associated with a pet")
public class TaskDTO {

    @Schema(description = "Unique identifier of the task", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Pet ID is required")
    @Schema(description = "The ID of the pet associated with the task", example = "101")
    private Long petId;

    @NotNull(message = "Task type is required")
    @Schema(description = "The type of task", example = "SURGERY", allowableValues = {"CHECKUP", "VACCINATION", "SURGERY"})
    private TaskType taskType;

    @NotBlank(message = "Description is required")
    @Schema(description = "A brief description of the task", example = "Surgery to treat leg fracture")
    private String description;

    @Future(message = "Deadline must be in the future")
    @Schema(description = "The deadline for the task completion", example = "2024-10-31T15:00:00")
    private LocalDateTime deadline;

    @NotNull(message = "Task status is required")
    @Schema(description = "The current status of the task", example = "PENDING", allowableValues = {"PENDING", "COMPLETED", "FAILED", "ESCALATED"})
    private TaskStatus status;
}
