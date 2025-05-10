package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.service.IMedicalRecordService;
import com.wolfhack.vetoptim.petmanagement.service.IPetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final IPetService IPetService;
    private final IMedicalRecordService IMedicalRecordService;

    @Async
    @KafkaListener(
        topics = "${kafka.topic.appointment.created}",
        groupId = "${kafka.consumer.group.appointment}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAppointmentCreated(@Payload AppointmentDTO appointmentDTO) {
        log.info("Processing appointment created event for Pet ID: {}", appointmentDTO.getPetId());
        IPetService.handleAppointmentCreated(appointmentDTO);

        if (appointmentDTO.getDiagnosis() != null && appointmentDTO.getTreatment() != null) {
            log.info("Creating medical record for pet ID: {} after appointment", appointmentDTO.getPetId());
            IMedicalRecordService.createMedicalRecordFromAppointment(
                appointmentDTO.getPetId(),
                appointmentDTO.getDiagnosis(),
                appointmentDTO.getTreatment()
            );
        } else {
            log.warn("No diagnosis or treatment provided for appointment ID: {}. Skipping medical record creation.", appointmentDTO.getId());
        }
    }

    @Async
    @KafkaListener(
        topics = "${kafka.topic.appointment.updated}",
        groupId = "${kafka.consumer.group.appointment}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAppointmentUpdated(@Payload AppointmentDTO appointmentDTO) {
        log.info("Processing appointment update event for Pet ID: {}", appointmentDTO.getPetId());
        IPetService.handleAppointmentUpdated(appointmentDTO);

        if (appointmentDTO.getDiagnosis() != null && appointmentDTO.getTreatment() != null) {
            log.info("Updating medical record for pet ID: {} after appointment update", appointmentDTO.getPetId());
            IMedicalRecordService.createMedicalRecordFromAppointment(
                appointmentDTO.getPetId(),
                appointmentDTO.getDiagnosis(),
                appointmentDTO.getTreatment()
            );
        } else {
            log.warn("No diagnosis or treatment provided for appointment update ID: {}. Skipping medical record update.", appointmentDTO.getId());
        }
    }
}
