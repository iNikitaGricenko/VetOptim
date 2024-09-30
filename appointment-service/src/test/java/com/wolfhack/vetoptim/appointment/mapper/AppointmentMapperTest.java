package com.wolfhack.vetoptim.appointment.mapper;

import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppointmentMapperTest {

    private final AppointmentMapper mapper = Mappers.getMapper(AppointmentMapper.class);

    @Test
    void testToDTO() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPetName("Fluffy");

        AppointmentDTO dto = mapper.toDTO(appointment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Fluffy", dto.getPetName());
    }

    @Test
    void testToEntity() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(1L);
        dto.setPetName("Fluffy");

        Appointment appointment = mapper.toEntity(dto);

        assertNotNull(appointment);
        assertEquals(1L, appointment.getId());
        assertEquals("Fluffy", appointment.getPetName());
    }
}