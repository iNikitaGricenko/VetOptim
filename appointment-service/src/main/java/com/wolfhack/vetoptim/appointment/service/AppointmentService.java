package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.appointment.client.OwnerClient;
import com.wolfhack.vetoptim.appointment.event.AppointmentEventPublisher;
import com.wolfhack.vetoptim.appointment.exception.AppointmentNotFoundException;
import com.wolfhack.vetoptim.appointment.mapper.AppointmentMapper;
import com.wolfhack.vetoptim.appointment.model.Appointment;
import com.wolfhack.vetoptim.appointment.repository.AppointmentRepository;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import com.wolfhack.vetoptim.common.dto.OwnerDTO;
import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
    private final OwnerClient ownerClient;
	private final AppointmentEventPublisher appointmentEventPublisher;
	private final NotificationService notificationService;
	private final AppointmentMapper appointmentMapper;

	public List<AppointmentDTO> getAppointmentsForPet(Long petId) {
		log.info("Fetching appointments for pet ID: {}", petId);
		return appointmentRepository.findAllByPetId(petId)
			.stream()
			.map(appointmentMapper::toDTO)
			.collect(Collectors.toList());
	}

	public Optional<AppointmentDTO> getAppointmentById(Long id) {
		log.info("Fetching appointment by ID: {}", id);
		return appointmentRepository.findById(id)
			.map(appointmentMapper::toDTO);
	}

	public List<AppointmentDTO> getAppointmentsForDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		log.info("Fetching appointments from {} to {}", startDate, endDate);
		return appointmentRepository.findByAppointmentDateBetween(startDate, endDate)
			.stream()
			.map(appointmentMapper::toDTO)
			.collect(Collectors.toList());
	}

    public AppointmentDTO createAppointment(Long ownerId, AppointmentDTO appointmentDTO) {
        log.info("Creating appointment for owner ID: {}", ownerId);

        if (!ownerClient.ownerExists(ownerId)) {
            throw new RuntimeException("Owner not found with ID: " + ownerId);
        }

        Appointment appointment = appointmentMapper.toEntity(appointmentDTO);
        appointment.setOwnerId(ownerId);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        publishTaskEvent(savedAppointment);

        if (Boolean.TRUE.equals(appointmentDTO.getRecurring()) && appointmentDTO.getRecurrenceInterval() != null) {
            scheduleRecurringAppointments(savedAppointment, appointmentDTO.getRecurrenceInterval());
        }

        String notificationMessage = String.format("Appointment scheduled for %s with veterinarian %s on %s.",
            savedAppointment.getPetName(), savedAppointment.getVeterinarianName(), savedAppointment.getAppointmentDate());
        notificationService.notifyOwnerOfAppointment(notificationMessage);

        return appointmentMapper.toDTO(savedAppointment);
    }

	public List<AppointmentDTO> searchAppointments(String veterinarianName, LocalDateTime startDate, LocalDateTime endDate, AppointmentStatus status) {
		log.info("Searching appointments with filters - Vet: {}, Date Range: {} to {}, Status: {}",
			veterinarianName, startDate, endDate, status);

		return appointmentRepository.findByVeterinarianNameAndAppointmentDateBetweenAndStatus(veterinarianName, startDate, endDate, status)
			.stream()
			.map(appointmentMapper::toDTO)
			.collect(Collectors.toList());
	}

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        log.info("Updating appointment ID: {}", id);
        return appointmentRepository.findById(id)
            .map(existingAppointment -> {
                appointmentMapper.updateAppointmentFromDTO(appointmentDTO, existingAppointment);
                Appointment updatedAppointment = appointmentRepository.save(existingAppointment);

                String notificationMessage = String.format("Appointment updated for %s with veterinarian %s on %s.",
                    updatedAppointment.getPetName(), updatedAppointment.getVeterinarianName(), updatedAppointment.getAppointmentDate());
                notificationService.notifyOwnerOfAppointment(notificationMessage);

                return appointmentMapper.toDTO(updatedAppointment);
            })
            .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    }

    public void updateOwnerInfoForAppointments(Long ownerId, OwnerDTO ownerDTO) {
        log.info("Updating owner information for all appointments of owner ID: {}", ownerId);
        List<Appointment> appointments = appointmentRepository.findAllByOwnerId(ownerId);

        for (Appointment appointment : appointments) {
            appointment.setOwnerName(ownerDTO.getName());
            appointmentRepository.save(appointment);
            log.info("Updated owner info for Appointment ID: {}", appointment.getId());
        }
    }

	public AppointmentDTO updateAppointmentStatus(Long id, AppointmentStatus newStatus) {
		log.info("Updating status for appointment ID: {}", id);
		return appointmentRepository.findById(id)
			.map(existingAppointment -> {
				existingAppointment.setStatus(newStatus);
				Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
				return appointmentMapper.toDTO(updatedAppointment);
			})
			.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
	}

	public AppointmentDTO rescheduleAppointment(Long id, LocalDateTime newDate) {
		log.info("Rescheduling appointment ID: {}", id);
		return appointmentRepository.findById(id)
			.map(existingAppointment -> {
				existingAppointment.setAppointmentDate(newDate);
				Appointment updatedAppointment = appointmentRepository.save(existingAppointment);

				String notificationMessage = String.format("Appointment for %s with veterinarian %s has been rescheduled to %s.",
					updatedAppointment.getPetName(), updatedAppointment.getVeterinarianName(), updatedAppointment.getAppointmentDate());
				notificationService.notifyOwnerOfAppointment(notificationMessage);

				return appointmentMapper.toDTO(updatedAppointment);
			})
			.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
	}

	public void deleteAppointment(Long id) {
		log.info("Deleting appointment ID: {}", id);
		appointmentRepository.findById(id).ifPresent(appointment -> {
			appointmentRepository.deleteById(id);
			String notificationMessage = String.format("Appointment for %s with veterinarian %s on %s has been canceled.",
				appointment.getPetName(), appointment.getVeterinarianName(), appointment.getAppointmentDate());
			notificationService.notifyOwnerOfAppointment(notificationMessage);
		});
	}

	private void scheduleRecurringAppointments(Appointment initialAppointment, int intervalDays) {
		log.info("Scheduling recurring appointments for appointment ID: {}", initialAppointment.getId());
		LocalDateTime nextAppointmentDate = initialAppointment.getAppointmentDate().plusDays(intervalDays);

		for (int i = 1; i <= 12; i++) {
			Appointment recurringAppointment = new Appointment();
			recurringAppointment.setPetId(initialAppointment.getPetId());
			recurringAppointment.setPetName(initialAppointment.getPetName());
			recurringAppointment.setVeterinarianName(initialAppointment.getVeterinarianName());
			recurringAppointment.setDescription(initialAppointment.getDescription());
			recurringAppointment.setAppointmentDate(nextAppointmentDate);
			recurringAppointment.setOwnerId(initialAppointment.getOwnerId());
			recurringAppointment.setOwnerName(initialAppointment.getOwnerName());
			recurringAppointment.setRecurring(true);
			recurringAppointment.setRecurrenceInterval(intervalDays);

			appointmentRepository.save(recurringAppointment);
			nextAppointmentDate = nextAppointmentDate.plusDays(intervalDays);
		}
	}

	private void publishTaskEvent(Appointment savedAppointment) {
		log.info("Publishing task creation event for appointment ID: {}", savedAppointment.getId());
		AppointmentTaskCreationEvent taskEvent = new AppointmentTaskCreationEvent(
			savedAppointment.getId(),
			savedAppointment.getPetId(),
			savedAppointment.getPetName(),
			savedAppointment.getVeterinarianName(),
			savedAppointment.getDescription(),
			savedAppointment.getAppointmentDate().toString()
		);
		appointmentEventPublisher.publishAppointmentTaskCreationEvent(taskEvent);
	}
}