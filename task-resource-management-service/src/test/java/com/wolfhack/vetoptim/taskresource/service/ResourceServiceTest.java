package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.mapper.ResourceMapper;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import com.wolfhack.vetoptim.taskresource.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceService resourceService;

    private Resource resource;
    private ResourceDTO resourceDTO;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setName("Vaccine");
        resource.setQuantity(10);

        resourceDTO = new ResourceDTO();
        resourceDTO.setName("Vaccine Updated");
    }

    @Test
    void getAllResources_Success() {
        resourceService.getAllResources();

        verify(resourceRepository).findAll();
    }

    @Test
    void createResource_Success() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        resourceService.createResource(resource);

        verify(resourceRepository).save(resource);
    }

    @Test
    void updateResource_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));  // Simulate finding the resource
        when(resourceMapper.updateResourceFromDTO(resourceDTO, resource)).thenReturn(resource);  // Return updated resource
        when(resourceRepository.save(resource)).thenReturn(resource);  // Simulate save returning the updated resource

        Resource updatedResource = resourceService.updateResource(1L, resourceDTO);

        verify(resourceRepository).save(resource);
        assertNotNull(updatedResource);  // Check if the resource is not null
        assertEquals(resource.getId(), updatedResource.getId());  // Validate that the IDs match
    }


    @Test
    void updateResource_ResourceNotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceService.updateResource(1L, resourceDTO));
    }

    @Test
    void partialUpdateResource_Success() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.partialUpdateResourceFromDTO(resourceDTO, resource)).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource updatedResource = resourceService.partialUpdateResource(1L, resourceDTO);

        verify(resourceRepository).save(resource);
        assertNotNull(updatedResource);
        assertEquals(resource.getId(), updatedResource.getId());
    }


    @Test
    void partialUpdateResource_ResourceNotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resourceService.partialUpdateResource(1L, resourceDTO));
    }

    @Test
    void deleteResource_Success() {
        resourceService.deleteResource(1L);

        verify(resourceRepository).deleteById(1L);
    }
}
