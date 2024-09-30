package com.wolfhack.vetoptim.common.event.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDepletedEvent {
    private String resourceName;
    private int remainingQuantity;
}