package com.wolfhack.vetoptim.common.event.pet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetDeletedEvent {
    private Long petId;
}