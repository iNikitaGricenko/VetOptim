package com.wolfhack.vetoptim.petmanagement.mapper;

import com.wolfhack.vetoptim.common.dto.pet.MedicalRecordDTO;
import com.wolfhack.vetoptim.petmanagement.model.MedicalRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicalRecordMapper {

	MedicalRecordDTO toDTO(MedicalRecord record);

	MedicalRecord toModel(MedicalRecordDTO dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void updateMedicalRecordFromDTO(MedicalRecord medicalRecordDetails, @MappingTarget MedicalRecord medicalRecord);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void updateMedicalRecordFromDTO(MedicalRecordDTO medicalRecordDetails, @MappingTarget MedicalRecord medicalRecord);

}
