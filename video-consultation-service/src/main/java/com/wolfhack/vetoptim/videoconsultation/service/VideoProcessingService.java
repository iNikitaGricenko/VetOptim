package com.wolfhack.vetoptim.videoconsultation.service;

import com.amazonaws.services.mediaconvert.AWSMediaConvert;
import com.amazonaws.services.mediaconvert.model.*;
import com.wolfhack.vetoptim.videoconsultation.model.NotificationType;
import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.repository.VideoSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingService {

	private final AWSMediaConvert mediaConvertClient;
	private final VideoSessionRepository videoSessionRepository;
	private final NotificationService notificationService;

	@Retryable(
		retryFor = {Exception.class},
		backoff = @Backoff(delay = 2000)
	)
	public void transcodeRecording(VideoSession session) {
		try {
			log.info("Starting transcoding for sessionId: {}", session.getId());
			String inputFileKey = session.getVideoUrl().substring(session.getVideoUrl().lastIndexOf("/") + 1);
			String outputFileKey = "transcoded-" + inputFileKey;

			CreateJobRequest jobRequest = new CreateJobRequest()
				.withRole("arn:aws:iam::YOUR_ACCOUNT_ID:role/MediaConvertRole")
				.withSettings(new JobSettings()
					.withInputs(new Input().withFileInput("s3://vetoptim-video-storage/" + inputFileKey))
					.withOutputGroups(new OutputGroup()
						.withOutputGroupSettings(new OutputGroupSettings()
							.withType(OutputGroupType.FILE_GROUP_SETTINGS))
						.withOutputs(new Output()
							.withContainerSettings(new ContainerSettings().withContainer(ContainerType.MP4))
							.withVideoDescription(new VideoDescription().withCodecSettings(new VideoCodecSettings().withCodec(VideoCodec.H_264)))
						)
					)
				);

			CreateJobResult result = mediaConvertClient.createJob(jobRequest);
			String jobId = result.getJob().getId();
			log.info("MediaConvert job started with Job ID: {}", jobId);

			String transcodedUrl = "https://vetoptim-video-storage.s3.amazonaws.com/transcoded/" + outputFileKey;
			session.setVideoUrl(transcodedUrl);
			session.setTranscodingJobId(jobId);
			videoSessionRepository.save(session);

			notificationService.sendNotification(session.getVeterinarianId(), session.getPetOwnerId(), session.getId(), NotificationType.TRANSCODING_COMPLETED, transcodedUrl);
			log.info("Transcoding job completed for sessionId: {}", session.getId());
		} catch (Exception e) {
			log.error("Error while starting MediaConvert job for sessionId: {}", session.getId(), e);
			throw e;
		}
	}

	@Recover
	public void recover(Exception e, VideoSession session) {
		log.error("Failed to transcode sessionId: {} after retries. Error: {}", session.getId(), e.getMessage());
	}
}