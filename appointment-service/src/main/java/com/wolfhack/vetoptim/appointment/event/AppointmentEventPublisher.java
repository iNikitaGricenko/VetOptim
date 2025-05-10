package com.wolfhack.vetoptim.appointment.event;

import com.wolfhack.vetoptim.common.event.appointment.AppointmentTaskCreationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.task.appointment}")
    private String taskAppointmentTopic;

    public void publishAppointmentTaskCreationEvent(AppointmentTaskCreationEvent event) {
        String key = "appointment-" + event.getAppointmentId();
        kafkaTemplate.send(taskAppointmentTopic, key, event);
    }
}
