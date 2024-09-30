package com.wolfhack.vetoptim.appointment.service;

import com.wolfhack.vetoptim.common.dto.AppointmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final AppointmentService appointmentService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routingKey.notification.reminder}")
    private String reminderRoutingKey;

    @Value("${reminder.days.before:1}")
    private int daysBeforeReminder;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendAppointmentReminders() {
        log.info("Sending appointment reminders...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusDays(daysBeforeReminder);

        List<AppointmentDTO> upcomingAppointments = appointmentService.getAppointmentsForDateRange(now, reminderThreshold);

        for (AppointmentDTO appointment : upcomingAppointments) {
            String message = String.format("Reminder: Appointment for %s with veterinarian %s is scheduled on %s.",
                    appointment.getPetName(), appointment.getVeterinarianName(), appointment.getAppointmentDate());
            rabbitTemplate.convertAndSend(notificationExchange, reminderRoutingKey, message);
            log.info("Reminder sent for appointment ID: {}", appointment.getPetId());
        }
    }
}