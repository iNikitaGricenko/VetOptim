package com.wolfhack.vetoptim.appointment.listener;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskCompletionListenerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private TaskCompletionListener taskCompletionListener;

    @Test
    void handleTaskCompletion_Success_CompletedStatus() {
        TaskCompletedEvent taskCompletedEvent = new TaskCompletedEvent(1L, 100L, "Surgery", "Task Completed", TaskStatus.COMPLETED);

        taskCompletionListener.handleTaskCompletion(taskCompletedEvent);

        verify(appointmentService, times(1)).updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);
    }

    @Test
    void handleTaskCompletion_Success_FailedStatus() {
        TaskCompletedEvent taskFailedEvent = new TaskCompletedEvent(1L, 100L, "Surgery", "Task Failed", TaskStatus.FAILED);

        taskCompletionListener.handleTaskCompletion(taskFailedEvent);

        verify(appointmentService, times(1)).updateAppointmentStatus(1L, AppointmentStatus.CANCELED);
    }

    @Test
    void handleTaskCompletion_Success_EscalatedStatus() {
        TaskCompletedEvent taskEscalatedEvent = new TaskCompletedEvent(1L, 100L, "Surgery", "Task Escalated", TaskStatus.ESCALATED);

        taskCompletionListener.handleTaskCompletion(taskEscalatedEvent);

        verify(appointmentService, times(1)).updateAppointmentStatus(1L, AppointmentStatus.ESCALATED);
    }

    @Test
    void handleTaskCompletion_ExceptionHandling() {
        TaskCompletedEvent taskCompletedEvent = new TaskCompletedEvent(1L, 100L, "Surgery", "Task Completed", TaskStatus.COMPLETED);
        doThrow(new RuntimeException("Appointment not found")).when(appointmentService).updateAppointmentStatus(anyLong(), any());

        taskCompletionListener.handleTaskCompletion(taskCompletedEvent);

        verify(appointmentService, times(1)).updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);
    }
}
