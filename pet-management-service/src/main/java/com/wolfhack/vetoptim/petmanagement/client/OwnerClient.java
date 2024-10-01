package com.wolfhack.vetoptim.petmanagement.client;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "owner-management-service", url = "${owner.service.url}")
public interface OwnerClient {

    @GetMapping("/owners/{id}/exists")
    boolean ownerExists(@PathVariable("id") Long id);

    @GetMapping("/owners/{id}")
    OwnerDTO getOwnerById(@PathVariable("id") Long id);
}