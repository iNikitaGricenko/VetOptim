package com.wolfhack.vetoptim.common.dto.pet;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO {

    private Long id;

    @NotBlank(message = "Pet name cannot be blank")
    private String name;

    @NotBlank(message = "Pet species cannot be blank")
    private String species;

    @NotBlank(message = "Pet breed cannot be blank")
    private String breed;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be 0 or greater")
    private Integer age;

    private String medicalHistory;

    @NotBlank(message = "Owner name cannot be blank")
    private String ownerName;

    @NotNull(message = "Owner ID cannot be null")
    private Long ownerId;
}
