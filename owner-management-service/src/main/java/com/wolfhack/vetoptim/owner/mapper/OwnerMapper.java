package com.wolfhack.vetoptim.owner.mapper;

import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.owner.model.Owner;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OwnerMapper {

    OwnerDTO toDTO(Owner owner);

    Owner toModel(OwnerDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOwnerFromDTO(OwnerDTO ownerDTO, @MappingTarget Owner owner);

}
