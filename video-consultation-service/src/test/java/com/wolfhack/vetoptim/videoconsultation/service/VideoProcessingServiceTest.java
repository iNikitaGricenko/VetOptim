package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.mediaconvert.AWSMediaConvert;
import com.amazonaws.services.mediaconvert.model.CreateJobRequest;
import com.amazonaws.services.mediaconvert.model.CreateJobResult;
import com.amazonaws.services.mediaconvert.model.Job;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void transcodeRecording_Success() {
        VideoSession session = new VideoSession();
        session.setId("12345");
        session.setVeterinarianId(1L);
        session.setPetOwnerId(2L);
        session.setVideoUrl("https://vetoptim-video-storage.s3.amazonaws.com/raw/video.mp4");

        CreateJobResult jobResult = mock(CreateJobResult.class);
        Job job = new Job();
        job.setId("job-123");

        when(mediaConvertClient.createJob(any(CreateJobRequest.class))).thenReturn(jobResult);
        when(jobResult.getJob()).thenReturn(job);

        videoProcessingService.transcodeRecording(session);

        verify(mediaConvertClient, times(1)).createJob(any(CreateJobRequest.class));
        verify(videoSessionRepository, times(1)).save(any(VideoSession.class));
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(2L), eq("12345"), eq(NotificationType.TRANSCODING_COMPLETED), anyString());
    }
}
