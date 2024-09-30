package com.wolfhack.vetoptim.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDTO {
    private Long id;
    private String name;
    private String contactDetails;
    private boolean notifyByEmail;
    private boolean notifyBySms;

    private List<Long> petIds;
    private List<Long> appointmentIds;
}
