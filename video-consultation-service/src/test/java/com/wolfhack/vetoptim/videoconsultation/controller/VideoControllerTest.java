package com.wolfhack.vetoptim.videoconsultation.controller;

import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    private VideoSession session;

    @BeforeEach
    void setUp() {
        session = new VideoSession();
        session.setId("testSessionId");
    }

    @Test
    void testStartSession() {
        when(videoService.startSession(anyLong(), anyLong())).thenReturn(session);

        ResponseEntity<VideoSession> response = videoController.startSession(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
    }

    @Test
    void testEndSession() {
        doNothing().when(videoService).endSession("testSessionId");

        ResponseEntity<Void> response = videoController.endSession("testSessionId");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(videoService).endSession("testSessionId");
    }

    @Test
    void testUploadRecording() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "testFile", "video/mp4", "test".getBytes());
        when(videoService.storeRecording(anyString(), any(File.class))).thenReturn("http://test-url.com");

        ResponseEntity<String> response = videoController.uploadRecording("testSessionId", file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://test-url.com", response.getBody());
    }

    @Test
    void testGetVideoRecording() {
        when(videoService.getVideoRecording(anyString())).thenReturn("http://test-url.com");

        ResponseEntity<String> response = videoController.getVideoRecording("testSessionId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://test-url.com", response.getBody());
    }
}
