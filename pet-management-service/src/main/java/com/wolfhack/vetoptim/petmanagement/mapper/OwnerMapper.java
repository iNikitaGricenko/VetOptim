package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.petmanagement.model.Owner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OwnerMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void updateOwnerFromDTO(Owner ownerDetails, @MappingTarget Owner owner);

}