package com.wolfhack.vetoptim.taskresource.service;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;

import java.util.List;

public interface IResourceService {

	List<ResourceDTO> getAllResources();

	ResourceDTO createResource(ResourceDTO resourceDTO);

	ResourceDTO updateResource(Long id, ResourceDTO resourceDTO);

	ResourceDTO partialUpdateResource(Long id, ResourceDTO resourceDTO);

	void deleteResource(Long id);

}
