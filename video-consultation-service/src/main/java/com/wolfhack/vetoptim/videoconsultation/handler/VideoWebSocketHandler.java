package com.wolfhack.vetoptim.videoconsultation.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfhack.vetoptim.common.dto.WebRTCSignal;
import com.wolfhack.vetoptim.videoconsultation.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoWebSocketHandler extends TextWebSocketHandler {

    private final VideoService videoService;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket connection established with sessionId: {}", sessionId);
        sessionMap.put(sessionId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebRTCSignal signal = objectMapper.readValue(message.getPayload(), WebRTCSignal.class);

        switch (signal.getType()) {
            case "offer":
                handleOffer(signal, session);
                break;
            case "answer":
                handleAnswer(signal, session);
                break;
            case "candidate":
                handleCandidate(signal, session);
                break;
            case "start":
                handleStartSignal(signal);
                break;
            case "end":
                handleEndSignal(signal);
                break;
            default:
                log.warn("Unknown WebRTC signal type: {}", signal.getType());
        }
    }

    private void handleOffer(WebRTCSignal offer, WebSocketSession senderSession) {
        log.info("Received SDP offer from sessionId: {}", senderSession.getId());
        relayMessageToPeer(offer, senderSession);
    }

    private void handleAnswer(WebRTCSignal answer, WebSocketSession senderSession) {
        log.info("Received SDP answer from sessionId: {}", senderSession.getId());
        relayMessageToPeer(answer, senderSession);
    }

    private void handleCandidate(WebRTCSignal candidate, WebSocketSession senderSession) {
        log.info("Received ICE candidate from sessionId: {}", senderSession.getId());
        relayMessageToPeer(candidate, senderSession);
    }

    private void handleStartSignal(WebRTCSignal signal) {
        Long vetId = signal.getVetId();
        Long ownerId = signal.getOwnerId();
        log.info("Starting video session for vetId: {} and ownerId: {}", vetId, ownerId);
        videoService.startSession(vetId, ownerId);
    }

    private void handleEndSignal(WebRTCSignal signal) {
        String sessionId = signal.getSessionId();
        log.info("Ending video session with ID: {}", sessionId);
        videoService.endSession(sessionId);
    }

    private void relayMessageToPeer(WebRTCSignal signal, WebSocketSession senderSession) {
        String targetSessionId = signal.getTargetSessionId();
        WebSocketSession targetSession = sessionMap.get(targetSessionId);

        if (targetSession != null && targetSession.isOpen()) {
            try {
                log.info("Relaying message from session {} to {}", senderSession.getId(), targetSessionId);
                targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(signal)));
            } catch (IOException e) {
                log.error("Error sending message to peer with sessionId: {}", targetSessionId, e);
            }
        } else {
            log.warn("Target WebSocket session not available or closed: {}", targetSessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket connection closed for sessionId: {}", sessionId);
        sessionMap.remove(sessionId).close();
        videoService.endSession(sessionId);
    }
}