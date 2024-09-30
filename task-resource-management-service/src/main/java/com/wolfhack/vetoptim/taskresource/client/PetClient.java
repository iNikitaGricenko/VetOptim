package com.wolfhack.vetoptim.taskresource.client;

import com.wolfhack.vetoptim.common.dto.PetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pet-management-service", url = "http://pet-management-service")
public interface PetClient {

    @GetMapping("/pets/{id}")
    PetDTO getPetById(@PathVariable("id") Long id);
}