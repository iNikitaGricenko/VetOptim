package com.wolfhack.vetoptim.videoconsultation.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhack.vetoptim.common.dto.WebRTCSignal;
import com.wolfhack.vetoptim.videoconsultation.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoWebSocketHandlerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoWebSocketHandler videoWebSocketHandler;

    @Mock
    private WebSocketSession webSocketSession;

    private WebRTCSignal signal;

    @BeforeEach
    void setUp() {
        signal = new WebRTCSignal();
        signal.setType("start");
        signal.setVetId(1L);
        signal.setOwnerId(1L);
    }

    @Test
    void testHandleTextMessage_startSignal() throws Exception {
        when(webSocketSession.getId()).thenReturn("testSessionId");

        videoWebSocketHandler.handleTextMessage(webSocketSession, new TextMessage(new ObjectMapper().writeValueAsString(signal)));

        verify(videoService).startSession(1L, 1L);
    }

    @Test
    void testHandleTextMessage_endSignal() throws Exception {
        signal.setType("end");
        signal.setSessionId("testSessionId");

        videoWebSocketHandler.handleTextMessage(webSocketSession, new TextMessage(new ObjectMapper().writeValueAsString(signal)));

        verify(videoService).endSession("testSessionId");
    }

    @Test
    void testAfterConnectionEstablished() throws Exception {
        when(webSocketSession.getId()).thenReturn("testSessionId");

        videoWebSocketHandler.afterConnectionEstablished(webSocketSession);

        verify(webSocketSession).getId();
    }

    @Test
    void testAfterConnectionClosed() throws Exception {
        when(webSocketSession.getId()).thenReturn("testSessionId");

        videoWebSocketHandler.afterConnectionClosed(webSocketSession, CloseStatus.NORMAL);

        verify(videoService).endSession("testSessionId");
    }
}
