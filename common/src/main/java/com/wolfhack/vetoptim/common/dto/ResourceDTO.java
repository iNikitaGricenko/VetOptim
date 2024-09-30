package com.wolfhack.vetoptim.common.dto;

import com.wolfhack.vetoptim.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    private String name;
    private ResourceType type;
    private int quantity;
}