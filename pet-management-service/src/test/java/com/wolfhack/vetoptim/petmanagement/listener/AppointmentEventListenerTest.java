package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AppointmentEventListenerTest {

	@Mock
	private PetService petService;

	@Mock
	private MedicalRecordService medicalRecordService;

	@InjectMocks
	private AppointmentEventListener appointmentEventListener;

	private AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testOnAppointmentCreated_WithDiagnosisAndTreatment() {
		AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setPetId(1L);
		appointmentDTO.setDiagnosis("Checkup");
		appointmentDTO.setTreatment("Treatment");

		appointmentEventListener.onAppointmentCreated(appointmentDTO);

		verify(petService).handleAppointmentCreated(appointmentDTO);
		verify(medicalRecordService).createMedicalRecordFromAppointment(appointmentDTO.getPetId(), appointmentDTO.getDiagnosis(), appointmentDTO.getTreatment());
	}

	@Test
	void testOnAppointmentCreated_WithoutDiagnosisAndTreatment() {
		AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setPetId(1L);

		appointmentEventListener.onAppointmentCreated(appointmentDTO);

		verify(petService).handleAppointmentCreated(appointmentDTO);
		verify(medicalRecordService, never()).createMedicalRecordFromAppointment(anyLong(), anyString(), anyString());
	}

	@Test
	void testOnAppointmentUpdated_WithDiagnosisAndTreatment() {
		AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setPetId(1L);
		appointmentDTO.setDiagnosis("Checkup");
		appointmentDTO.setTreatment("Treatment");

		appointmentEventListener.onAppointmentUpdated(appointmentDTO);

		verify(petService).handleAppointmentUpdated(appointmentDTO);
		verify(medicalRecordService).createMedicalRecordFromAppointment(appointmentDTO.getPetId(), appointmentDTO.getDiagnosis(), appointmentDTO.getTreatment());
	}

	@Test
	void testOnAppointmentUpdated_WithoutDiagnosisAndTreatment() {
		AppointmentDTO appointmentDTO = new AppointmentDTO();
		appointmentDTO.setPetId(1L);

		appointmentEventListener.onAppointmentUpdated(appointmentDTO);

		verify(petService).handleAppointmentUpdated(appointmentDTO);
		verify(medicalRecordService, never()).createMedicalRecordFromAppointment(anyLong(), anyString(), anyString());
	}
}