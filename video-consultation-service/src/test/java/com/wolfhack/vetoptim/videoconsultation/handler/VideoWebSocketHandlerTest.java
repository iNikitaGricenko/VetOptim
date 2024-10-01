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
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoWebSocketHandlerTest {

    @Mock
    private VideoService videoService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private VideoWebSocketHandler videoWebSocketHandler;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void afterConnectionEstablished_shouldPutSessionInMap() throws Exception {
        when(session.getId()).thenReturn("12345");

        videoWebSocketHandler.afterConnectionEstablished(session);

        verify(session, times(1)).getId();
    }

    @Test
    void handleTextMessage_startSignal_shouldInvokeStartSession() throws Exception {
        WebRTCSignal signal = new WebRTCSignal();
        signal.setType("start");
        signal.setVetId(1L);
        signal.setOwnerId(2L);

        String signalPayload = objectMapper.writeValueAsString(signal);
        TextMessage message = new TextMessage(signalPayload);

        videoWebSocketHandler.handleTextMessage(session, message);

        verify(videoService, times(1)).startSession(1L, 2L);
    }

    @Test
    void handleTextMessage_endSignal_shouldInvokeEndSession() throws Exception {
        WebRTCSignal signal = new WebRTCSignal();
        signal.setType("end");
        signal.setSessionId("12345");

        String signalPayload = objectMapper.writeValueAsString(signal);
        TextMessage message = new TextMessage(signalPayload);

        videoWebSocketHandler.handleTextMessage(session, message);

        verify(videoService, times(1)).endSession("12345");
    }

    @Test
    void afterConnectionClosed_shouldRemoveSessionFromMapAndEndSession() throws Exception {
        when(session.getId()).thenReturn("12345");

        videoWebSocketHandler.afterConnectionEstablished(session);

        videoWebSocketHandler.afterConnectionClosed(session, null);

        verify(videoService, times(1)).endSession("12345");
        verify(session, times(1)).close();
    }
}