package com.wolfhack.vetoptim.billing.client;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "owner-service")
public interface OwnerClient {

    @GetMapping("/api/owners/{id}")
    OwnerDTO getOwnerById(@PathVariable("id") Long id);
}
