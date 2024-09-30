package com.wolfhack.vetoptim.taskresource.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class ResourceEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ResourceEventListener resourceEventListener;

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
    void testHandleResourceDepleted() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        resourceEventListener.handleResourceDepleted(event);

        verify(notificationService).notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());
    }
}