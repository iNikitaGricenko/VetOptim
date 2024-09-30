package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.petmanagement.model.Pet;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PetMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	Pet updatePetFromDTO(Pet petDetails, @MappingTarget Pet pet);

}