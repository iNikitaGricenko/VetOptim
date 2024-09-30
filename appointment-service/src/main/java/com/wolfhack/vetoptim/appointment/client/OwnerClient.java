package com.wolfhack.vetoptim.appointment.client;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "owner-management-service", url = "${owner.service.url}")
public interface OwnerClient {

    @GetMapping("/owners/{id}/exists")
    boolean ownerExists(@PathVariable Long id);

    @GetMapping("/owners/{id}")
    OwnerDTO getOwnerById(@PathVariable Long id);
}