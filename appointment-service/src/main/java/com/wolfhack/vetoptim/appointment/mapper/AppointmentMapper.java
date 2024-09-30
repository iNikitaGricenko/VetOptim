package com.wolfhack.vetoptim.appointment.mapper;


import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    AppointmentDTO toDTO(Appointment appointment);

    Appointment toEntity(AppointmentDTO appointmentDTO);

    void updateAppointmentFromDTO(AppointmentDTO dto, @MappingTarget Appointment appointment);
}