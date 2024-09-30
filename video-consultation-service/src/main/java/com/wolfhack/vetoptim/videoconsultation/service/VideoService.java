package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoSessionRepository videoSessionRepository;
    private final AmazonS3 s3Client;
    private final VideoProcessingService videoProcessingService;
    private final NotificationService notificationService;

    public VideoSession startSession(Long vetId, Long ownerId) {
        log.info("Starting video session for vetId: {} and ownerId: {}", vetId, ownerId);
        VideoSession session = new VideoSession();
        session.setVeterinarianId(vetId);
        session.setPetOwnerId(ownerId);
        session.setStartTime(LocalDateTime.now());
        VideoSession savedSession = videoSessionRepository.save(session);

        notificationService.sendNotification(vetId, ownerId, savedSession.getId(), NotificationType.SESSION_START, null);
        log.info("Session started with ID: {}", savedSession.getId());
        return savedSession;
    }

    public synchronized void endSession(String sessionId) {
        log.info("Ending video session with ID: {}", sessionId);
        VideoSession session = videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getEndTime() != null) {
            log.warn("Session already ended for sessionId: {}", sessionId);
            return;
        }

        session.setEndTime(LocalDateTime.now());
        videoSessionRepository.save(session);

        notificationService.sendNotification(session.getVeterinarianId(), session.getPetOwnerId(), sessionId, NotificationType.SESSION_END, null);
        log.info("Session ended for sessionId: {}", sessionId);

        videoProcessingService.transcodeRecording(session);
    }

    public String storeRecording(String sessionId, File videoFile) {
        String fileUrl = uploadToS3(videoFile);
        VideoSession session = videoSessionRepository.findById(sessionId).orElseThrow();
        session.setVideoUrl(fileUrl);
        session.setRecorded(true);
        videoSessionRepository.save(session);

        notificationService.sendNotification(session.getVeterinarianId(), session.getPetOwnerId(), sessionId, NotificationType.RECORDING_UPLOADED, "Recording uploaded successfully.");
        log.info("Recording stored for sessionId: {} at URL: {}", sessionId, fileUrl);
        return fileUrl;
    }

    public VideoSession getSession(String sessionId) {
        log.info("Fetching video session with ID: {}", sessionId);
        return videoSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public String getVideoRecording(String sessionId) {
        VideoSession session = getSession(sessionId);
        if (!session.isRecorded()) {
            log.error("No recording found for session: {}", sessionId);
            throw new RuntimeException("No recording found for session: " + sessionId);
        }
        log.info("Returning video URL for sessionId: {}", sessionId);
        return session.getVideoUrl();
    }

    private String uploadToS3(File videoFile) {
        String bucketName = "vetoptim-video-storage";
        String keyName = videoFile.getName();

        PutObjectResult result = s3Client.putObject(
            new PutObjectRequest(bucketName, keyName, videoFile)
        );

        log.info("File uploaded to S3 with key: {}", keyName);
        return s3Client.getUrl(bucketName, keyName).toString();
    }
}
