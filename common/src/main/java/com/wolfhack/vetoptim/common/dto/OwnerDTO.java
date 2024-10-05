package com.wolfhack.vetoptim.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDTO {

    private Long id;

    @NotBlank(message = "Owner name cannot be blank")
    private String name;

    @NotBlank(message = "Contact details cannot be blank")
    @Email(message = "Contact details must be a valid email")
    private String contactDetails;

    private boolean notifyByEmail;

    private boolean notifyBySms;

    private List<Long> petIds;

    private List<Long> appointmentIds;
}
