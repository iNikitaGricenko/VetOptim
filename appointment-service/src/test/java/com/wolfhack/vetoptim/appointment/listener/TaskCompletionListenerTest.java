package com.wolfhack.vetoptim.appointment.listener;

import com.wolfhack.vetoptim.appointment.service.AppointmentService;
import com.wolfhack.vetoptim.common.AppointmentStatus;
import com.wolfhack.vetoptim.common.TaskStatus;
import com.wolfhack.vetoptim.common.event.task.TaskCompletedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class TaskCompletionListenerTest {

	@Mock
	private AppointmentService appointmentService;

	@InjectMocks
	private TaskCompletionListener taskCompletionListener;

	private AutoCloseable openedMocks;

	@BeforeEach
	void setUp() {
		openedMocks = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		openedMocks.close();
	}

	@Test
	public void testHandleTaskCompletion_TaskCompleted() {
		TaskCompletedEvent event = new TaskCompletedEvent(1L, 101L, "CHECKUP", "Routine Checkup", TaskStatus.COMPLETED);

		taskCompletionListener.handleTaskCompletion(event);

		verify(appointmentService).updateAppointmentStatus(1L, AppointmentStatus.COMPLETED);
	}

	@Test
	public void testHandleTaskCompletion_TaskEscalated() {
		TaskCompletedEvent event = new TaskCompletedEvent(2L, 102L, "SURGERY", "Emergency Surgery", TaskStatus.ESCALATED);

		taskCompletionListener.handleTaskCompletion(event);

		verify(appointmentService).updateAppointmentStatus(2L, AppointmentStatus.ESCALATED);
	}

	@Test
	public void testHandleTaskCompletion_TaskFailed() {
		TaskCompletedEvent event = new TaskCompletedEvent(3L, 103L, "DENTAL", "Dental Cleaning", TaskStatus.FAILED);

		taskCompletionListener.handleTaskCompletion(event);

		verify(appointmentService).updateAppointmentStatus(3L, AppointmentStatus.CANCELED);
	}
}