package com.wolfhack.vetoptim.billing.service;

import com.wolfhack.vetoptim.billing.model.ResourceCost;
import com.wolfhack.vetoptim.billing.repository.ResourceCostRepository;
import com.wolfhack.vetoptim.common.dto.billing.ResourceCostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceCostService {

    private final ResourceCostRepository resourceCostRepository;

    public ResourceCost saveResourceCost(ResourceCostDTO resourceCostDTO) {
        ResourceCost resourceCost = new ResourceCost();
        resourceCost.setResourceId(resourceCostDTO.getResourceId());
        resourceCost.setResourceName(resourceCostDTO.getResourceName());
        resourceCost.setCost(resourceCostDTO.getCost());
        return resourceCostRepository.save(resourceCost);
    }

    public Optional<ResourceCost> getResourceCost(Long resourceId) {
        return resourceCostRepository.findByResourceId(resourceId);
    }
}