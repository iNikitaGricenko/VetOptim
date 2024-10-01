package com.wolfhack.vetoptim.owner.listener;

import com.wolfhack.vetoptim.common.event.pet.PetCreatedEvent;
import com.wolfhack.vetoptim.owner.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetEventListenerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private PetEventListener petEventListener;

    @Test
    void testHandlePetCreatedEvent_Success() {
        PetCreatedEvent event = new PetCreatedEvent(100L, "Max", "Dog", "Bulldog", 1L);

        petEventListener.handlePetCreatedEvent(event);

        verify(ownerService, times(1)).addPetToOwner(1L, 100L);
    }
}