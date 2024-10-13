package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.common.dto.pet.VaccinationRequestDTO;
import com.wolfhack.vetoptim.common.dto.pet.VaccinationResponseDTO;
import com.wolfhack.vetoptim.petmanagement.model.Vaccination;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VaccinationMapper {

    VaccinationResponseDTO toDTO(Vaccination vaccination);

    Vaccination toModel(VaccinationRequestDTO vaccinationRequestDTO);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateModelFromDTO(VaccinationRequestDTO vaccinationRequestDTO, @MappingTarget Vaccination vaccination);

}