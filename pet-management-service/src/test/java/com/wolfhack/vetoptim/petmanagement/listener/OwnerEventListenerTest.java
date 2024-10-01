package com.wolfhack.vetoptim.petmanagement.listener;

import com.wolfhack.vetoptim.common.event.owner.OwnerCreatedEvent;
import com.wolfhack.vetoptim.petmanagement.service.PetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OwnerEventListenerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private OwnerEventListener ownerEventListener;

    @Test
    void handleOwnerCreatedEvent_success() {
        OwnerCreatedEvent ownerCreatedEvent = new OwnerCreatedEvent(1L, "John Doe", "example@mail.com");

        ownerEventListener.handleOwnerCreatedEvent(ownerCreatedEvent);

        verify(petService).updateOwnerInfoForPets(1L, "John Doe");
    }

    @Test
    void handleOwnerCreatedEvent_logsCorrectInformation() {
        OwnerCreatedEvent ownerCreatedEvent = new OwnerCreatedEvent(1L, "John Doe", "example@mail.com");

        ownerEventListener.handleOwnerCreatedEvent(ownerCreatedEvent);

        verify(petService).updateOwnerInfoForPets(1L, "John Doe");
    }
}