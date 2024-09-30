package com.wolfhack.vetoptim.common.event.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalTaskCreationEvent {
    private Long petId;
    private String petName;
    private String ownerName;
    private String taskType;
    private String description;
}