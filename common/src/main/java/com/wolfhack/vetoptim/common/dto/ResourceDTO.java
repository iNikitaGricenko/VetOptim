package com.wolfhack.vetoptim.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing a resource that is used in tasks, such as medical supplies or equipment")
public class ResourceDTO {

    @Schema(description = "Unique identifier of the resource", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Resource name cannot be blank")
    @Schema(description = "The name of the resource", example = "Vaccination Kit")
    private String name;

    @NotNull(message = "Resource type is required")
    @Schema(description = "The type of resource", example = "MEDICAL")
    private String type;

    @Min(value = 0, message = "Quantity must be 0 or greater")
    @Schema(description = "The available quantity of the resource", example = "100")
    private int quantity;
}