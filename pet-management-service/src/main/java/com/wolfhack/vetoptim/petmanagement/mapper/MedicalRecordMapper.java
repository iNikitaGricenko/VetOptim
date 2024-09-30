package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicalRecordMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void updateMedicalRecordFromDTO(MedicalRecord medicalRecordDetails, @MappingTarget MedicalRecord medicalRecord);

}
