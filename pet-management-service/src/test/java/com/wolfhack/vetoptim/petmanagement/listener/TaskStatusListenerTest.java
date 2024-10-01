package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusListenerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private TaskStatusListener taskStatusListener;

    @Test
    void handleTaskCompleted_SurgeryTask() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 1L, "Surgery", "Surgery on left leg", TaskStatus.COMPLETED);

        taskStatusListener.handleTaskCompleted(event);

        verify(medicalRecordService, times(1)).createMedicalRecordFromAppointment(1L, "Surgery Completed", "Surgery on left leg");
    }

    @Test
    void handleTaskCompleted_NonSurgeryTask() {
        TaskCompletedEvent event = new TaskCompletedEvent(1L, 1L, "Checkup", "General checkup", TaskStatus.COMPLETED);

        taskStatusListener.handleTaskCompleted(event);

        verify(medicalRecordService, never()).createMedicalRecordFromAppointment(anyLong(), anyString(), anyString());
    }
}