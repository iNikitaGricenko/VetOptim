package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.petmanagement.service.IMedicalRecordService;
import com.wolfhack.vetoptim.petmanagement.service.IPetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final IPetService IPetService;
    private final IMedicalRecordService IMedicalRecordService;

    @Async
    @RabbitListener(queues = "${rabbitmq.queue.appointment.created}")
    public void onAppointmentCreated(AppointmentDTO appointmentDTO) {
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
    @RabbitListener(queues = "${rabbitmq.queue.appointment.updated}")
    public void onAppointmentUpdated(AppointmentDTO appointmentDTO) {
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
