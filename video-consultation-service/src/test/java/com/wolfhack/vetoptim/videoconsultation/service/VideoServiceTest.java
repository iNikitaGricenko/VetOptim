package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoSessionRepository videoSessionRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private VideoProcessingService videoProcessingService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VideoService videoService;

    @Test
    void startSession_Success() {
        Long vetId = 1L;
        Long ownerId = 2L;
        VideoSession session = new VideoSession();
        session.setId("12345");
        session.setVeterinarianId(vetId);
        session.setPetOwnerId(ownerId);
        session.setStartTime(LocalDateTime.now());

        when(videoSessionRepository.save(any(VideoSession.class))).thenReturn(session);

        videoService.startSession(vetId, ownerId);

        verify(videoSessionRepository, times(1)).save(any(VideoSession.class));
        verify(notificationService, times(1)).sendNotification(vetId, ownerId, session.getId(), NotificationType.SESSION_START, null);
    }

    @Test
    void endSession_Success() {
        VideoSession session = new VideoSession();
        session.setId("12345");
        session.setVeterinarianId(1L);
        session.setPetOwnerId(2L);
        session.setEndTime(null);

        when(videoSessionRepository.findById(anyString())).thenReturn(Optional.of(session));

        videoService.endSession("12345");

        verify(videoSessionRepository, times(1)).save(any(VideoSession.class));
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(2L), anyString(), eq(NotificationType.SESSION_END), isNull());
        verify(videoProcessingService, times(1)).transcodeRecording(any(VideoSession.class));
    }

    @Test
    void storeRecording_Success() throws Exception {
        VideoSession session = new VideoSession();
        session.setId("12345");
        session.setVeterinarianId(1L);
        session.setPetOwnerId(2L);
        File videoFile = new File("video.mp4");
        String expectedUrl = "https://vetoptim-video-storage.s3.amazonaws.com/video.mp4";

        when(videoSessionRepository.findById(anyString())).thenReturn(Optional.of(session));
        when(s3Client.putObject(any())).thenReturn(new PutObjectResult());
        when(s3Client.getUrl(anyString(), anyString())).thenReturn(new URL(expectedUrl));

        String actualUrl = videoService.storeRecording(session.getId(), videoFile);

        assertEquals(expectedUrl, actualUrl);
        verify(videoSessionRepository, times(1)).save(any(VideoSession.class));
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(2L), eq("12345"), eq(NotificationType.RECORDING_UPLOADED), anyString());
    }
}
