package com.wolfhack.vetoptim.common.event.pet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetCreatedEvent {
    private Long petId;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;
}