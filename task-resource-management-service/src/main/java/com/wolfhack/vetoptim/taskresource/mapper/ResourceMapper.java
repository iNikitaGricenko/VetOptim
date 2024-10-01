package com.wolfhack.vetoptim.taskresource.mapper;

import com.wolfhack.vetoptim.common.dto.ResourceDTO;
import com.wolfhack.vetoptim.taskresource.model.Resource;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResourceMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	Resource updateResourceFromDTO(ResourceDTO resourceDTO, @MappingTarget Resource resource);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Resource partialUpdateResourceFromDTO(ResourceDTO resourceDTO, @MappingTarget Resource resource);
}