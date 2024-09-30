package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.mediaconvert.AWSMediaConvert;
import com.amazonaws.services.mediaconvert.model.CreateJobRequest;
import com.amazonaws.services.mediaconvert.model.CreateJobResult;
import com.amazonaws.services.mediaconvert.model.Job;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoProcessingServiceTest {

    @Mock
    private AWSMediaConvert mediaConvertClient;

    @Mock
    private VideoSessionRepository videoSessionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VideoProcessingService videoProcessingService;

    private VideoSession session;

    @BeforeEach
    void setUp() {
        session = new VideoSession();
        session.setId("testSessionId");
        session.setVeterinarianId(1L);
        session.setPetOwnerId(1L);
        session.setVideoUrl("https://vetoptim-video-storage.s3.amazonaws.com/testFile");
    }

    @Test
    void testTranscodeRecording() {
        when(mediaConvertClient.createJob(any(CreateJobRequest.class))).thenReturn(new CreateJobResult().withJob(new Job().withId("jobId")));

        videoProcessingService.transcodeRecording(session);

        verify(notificationService).sendNotification(1L, 1L, "testSessionId", NotificationType.TRANSCODING_COMPLETED, "https://vetoptim-video-storage.s3.amazonaws.com/transcoded/testFile");
        verify(videoSessionRepository).save(any(VideoSession.class));
    }

    @Test
    void testTranscodingFailure() {
        when(mediaConvertClient.createJob(any(CreateJobRequest.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> videoProcessingService.transcodeRecording(session));

        verifyNoInteractions(notificationService);
    }
}
