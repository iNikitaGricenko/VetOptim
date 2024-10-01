package com.wolfhack.vetoptim.videoconsultation.controller;

import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();
    }

    @Test
    void startSession_shouldReturnOk() throws Exception {
        VideoSession session = new VideoSession();
        session.setId("12345");

        when(videoService.startSession(1L, 2L)).thenReturn(session);

        mockMvc.perform(post("/video-sessions/start")
                .param("vetId", "1")
                .param("ownerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("12345"));

        verify(videoService, times(1)).startSession(1L, 2L);
    }

    @Test
    void endSession_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/video-sessions/end")
                .param("sessionId", "12345"))
                .andExpect(status().isNoContent());

        verify(videoService, times(1)).endSession("12345");
    }

    @Test
    void uploadRecording_shouldReturnFileUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", MediaType.MULTIPART_FORM_DATA_VALUE, "video content".getBytes());
        String expectedUrl = "https://s3.amazonaws.com/video-storage/video.mp4";

        when(videoService.storeRecording(anyString(), any(File.class))).thenReturn(expectedUrl);

        mockMvc.perform(multipart("/video-sessions/record")
                .file(file)
                .param("sessionId", "12345"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));

        verify(videoService, times(1)).storeRecording(anyString(), any(File.class));
    }

    @Test
    void getVideoRecording_shouldReturnVideoUrl() throws Exception {
        String expectedUrl = "https://s3.amazonaws.com/video-storage/video.mp4";

        when(videoService.getVideoRecording("12345")).thenReturn(expectedUrl);

        mockMvc.perform(get("/video-sessions/12345/recording"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));

        verify(videoService, times(1)).getVideoRecording("12345");
    }
}
