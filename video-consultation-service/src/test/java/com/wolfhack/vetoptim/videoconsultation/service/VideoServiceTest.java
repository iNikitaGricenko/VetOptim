package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private VideoSession session;

    @BeforeEach
    void setUp() {
        session = new VideoSession();
        session.setId("testSessionId");
        session.setVeterinarianId(1L);
        session.setPetOwnerId(1L);
    }

    @Test
    void testStartSession() {
        when(videoSessionRepository.save(any(VideoSession.class))).thenReturn(session);

        VideoSession createdSession = videoService.startSession(1L, 1L);

        assertNotNull(createdSession);
        assertEquals(session.getId(), createdSession.getId());
        verify(notificationService).sendNotification(1L, 1L, session.getId(), NotificationType.SESSION_START, null);
        verify(videoSessionRepository).save(any(VideoSession.class));
    }

    @Test
    void testEndSession() {
        when(videoSessionRepository.findById("testSessionId")).thenReturn(Optional.of(session));

        videoService.endSession("testSessionId");

        assertNotNull(session.getEndTime());
        verify(notificationService).sendNotification(1L, 1L, "testSessionId", NotificationType.SESSION_END, null);
        verify(videoProcessingService).transcodeRecording(session);
    }

    @Test
    void testStoreRecording() {
        when(videoSessionRepository.findById("testSessionId")).thenReturn(Optional.of(session));
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        String fileUrl = videoService.storeRecording("testSessionId", new File("testFile"));

        assertNotNull(fileUrl);
        verify(notificationService).sendNotification(1L, 1L, "testSessionId", NotificationType.RECORDING_UPLOADED, "Recording uploaded successfully.");
        verify(videoSessionRepository).save(any(VideoSession.class));
    }
}
