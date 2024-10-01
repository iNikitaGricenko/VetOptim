package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.resource.ResourceDepletedEvent;
import com.wolfhack.vetoptim.petmanagement.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ResourceListener resourceListener;

    @Test
    void handleResourceDepletion_success() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Vaccine", 10);

        resourceListener.handleResourceDepletion(event);

        verify(notificationService).notifyOfResourceDepletion("Vaccine", 10);
    }

    @Test
    void handleResourceDepletion_logsCorrectInformation() {
        ResourceDepletedEvent event = new ResourceDepletedEvent("Medicine", 5);

        resourceListener.handleResourceDepletion(event);

        verify(notificationService).notifyOfResourceDepletion("Medicine", 5);
    }
}