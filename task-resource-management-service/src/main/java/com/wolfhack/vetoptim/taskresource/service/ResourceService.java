package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.client.BillingClient;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceMapper;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService implements IResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final BillingClient billingClient;

    @Override
    public List<ResourceDTO> getAllResources() {
        log.info("Fetching all resources");
        return resourceRepository.findAll()
            .stream()
            .map(resourceMapper::toDTO)
            .toList();
    }

    @Override
    public ResourceDTO createResource(ResourceDTO resourceDTO) {
        log.info("Creating new resource: {}", resourceDTO.getName());
        Resource resource = resourceMapper.toModel(resourceDTO);
        Resource savedResource = resourceRepository.save(resource);
        log.info("Resource created with ID: {}", savedResource.getId());
        return resourceMapper.toDTO(savedResource);
    }

    @Override
    public ResourceDTO updateResource(Long id, ResourceDTO resourceDTO) {
        log.info("Updating resource with ID: {}", id);
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + id));

        resourceMapper.updateResourceFromDTO(resourceDTO, resource);
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource updated successfully with ID: {}", updatedResource.getId());
        return resourceMapper.toDTO(updatedResource);
    }

    @Override
    public ResourceDTO partialUpdateResource(Long id, ResourceDTO resourceDTO) {
        log.info("Partially updating resource with ID: {}", id);
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + id));

        resourceMapper.partialUpdateResourceFromDTO(resourceDTO, resource);
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource partially updated with ID: {}", updatedResource.getId());
        return resourceMapper.toDTO(updatedResource);
    }

    @Override
    public void deleteResource(Long id) {
        log.info("Deleting resource with ID: {}", id);
        resourceRepository.deleteById(id);
        log.info("Resource deleted with ID: {}", id);
    }
}
