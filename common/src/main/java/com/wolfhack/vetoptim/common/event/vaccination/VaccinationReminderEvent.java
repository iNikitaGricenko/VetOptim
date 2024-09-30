package com.wolfhack.vetoptim.common.event.vaccination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationReminderEvent {
    private Long petId;
    private String petName;
    private Long ownerId;
    private String vaccineName;
    private String reminderType;
}