package com.wolfhack.vetoptim.common.event.owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDeletedEvent {
    private Long ownerId;
}