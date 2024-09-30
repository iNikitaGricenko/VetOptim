package com.wolfhack.vetoptim.taskresource.mapper;

import com.wolfhack.vetoptim.common.dto.ResourceUsageDTO;
import com.wolfhack.vetoptim.taskresource.model.ResourceUsage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResourceUsedMapper {

	List<ResourceUsage> toModel(List<ResourceUsageDTO> dtos);

	List<ResourceUsageDTO> toDTO(List<ResourceUsage> resourceUsages);

}