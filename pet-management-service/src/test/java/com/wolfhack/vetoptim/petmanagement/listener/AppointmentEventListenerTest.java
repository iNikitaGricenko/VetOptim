package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @Mock
    private PetService petService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private AppointmentEventListener appointmentEventListener;

    @Test
    void onAppointmentCreated_withDiagnosisAndTreatment_createsMedicalRecord() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setDiagnosis("Cold");
        appointmentDTO.setTreatment("Medication");

        appointmentEventListener.onAppointmentCreated(appointmentDTO);

        verify(petService).handleAppointmentCreated(any(AppointmentDTO.class));
        verify(medicalRecordService).createMedicalRecordFromAppointment(
            eq(1L), eq("Cold"), eq("Medication"));
    }

    @Test
    void onAppointmentCreated_withoutDiagnosisOrTreatment_skipsMedicalRecordCreation() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setDiagnosis(null);
        appointmentDTO.setTreatment(null);

        appointmentEventListener.onAppointmentCreated(appointmentDTO);

        verify(petService).handleAppointmentCreated(any(AppointmentDTO.class));
        verify(medicalRecordService, never()).createMedicalRecordFromAppointment(anyLong(), any(), any());
    }

    @Test
    void onAppointmentUpdated_withDiagnosisAndTreatment_updatesMedicalRecord() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setDiagnosis("Fever");
        appointmentDTO.setTreatment("Antibiotics");

        appointmentEventListener.onAppointmentUpdated(appointmentDTO);

        verify(petService).handleAppointmentUpdated(any(AppointmentDTO.class));
        verify(medicalRecordService).createMedicalRecordFromAppointment(
            eq(1L), eq("Fever"), eq("Antibiotics"));
    }

    @Test
    void onAppointmentUpdated_withoutDiagnosisOrTreatment_skipsMedicalRecordUpdate() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPetId(1L);
        appointmentDTO.setDiagnosis(null);
        appointmentDTO.setTreatment(null);

        appointmentEventListener.onAppointmentUpdated(appointmentDTO);

        verify(petService).handleAppointmentUpdated(any(AppointmentDTO.class));
        verify(medicalRecordService, never()).createMedicalRecordFromAppointment(anyLong(), any(), any());
    }
}