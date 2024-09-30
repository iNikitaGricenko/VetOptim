package com.wolfhack.vetoptim.common.dto.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceBillingRequest {
    private Long resourceId;
    private Long taskId;
    private String resourceName;
    private int quantityUsed;
}