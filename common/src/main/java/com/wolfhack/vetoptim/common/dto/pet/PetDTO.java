package com.wolfhack.vetoptim.common.dto.pet;

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
@Schema(description = "DTO for transferring pet details")
public class PetDTO {

    @Schema(description = "The ID of the pet", example = "1")
    private Long id;

    @Schema(description = "The name of the pet", example = "Buddy")
    @NotBlank(message = "Pet name cannot be blank")
    private String name;

    @Schema(description = "The species of the pet", example = "Dog")
    @NotBlank(message = "Pet species cannot be blank")
    private String species;

    @Schema(description = "The breed of the pet", example = "Golden Retriever")
    @NotBlank(message = "Pet breed cannot be blank")
    private String breed;

    @Schema(description = "The age of the pet in years", example = "3")
    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be 0 or greater")
    private Integer age;

    @Schema(description = "Medical history of the pet", example = "No major issues")
    private String medicalHistory;

    @Schema(description = "The name of the owner", example = "John Doe")
    @NotBlank(message = "Owner name cannot be blank")
    private String ownerName;

    @Schema(description = "The ID of the owner", example = "1001")
    @NotNull(message = "Owner ID cannot be null")
    private Long ownerId;
}
