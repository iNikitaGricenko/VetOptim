package com.wolfhack.vetoptim.taskresource.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.taskresource.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ResourceEventListener resourceEventListener;

    @Test
    void handleResourceDepleted_Success() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Surgical Kit", 2);

        resourceEventListener.handleResourceDepleted(event);

        verify(notificationService, times(1)).notifyOfResourceDepletion(event.getResourceName(), event.getRemainingQuantity());
    }
}