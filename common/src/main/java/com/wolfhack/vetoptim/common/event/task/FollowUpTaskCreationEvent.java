package com.wolfhack.vetoptim.common.event.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpTaskCreationEvent {
    private Long petId;
    private String petName;
    private String followUpDescription;
    private String followUpDate;
}
