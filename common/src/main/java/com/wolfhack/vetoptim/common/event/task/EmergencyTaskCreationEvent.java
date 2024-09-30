package com.wolfhack.vetoptim.common.event.task;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyTaskCreationEvent {
    private Long petId;
    private String petName;
    private String condition;
    private String taskDescription;
}