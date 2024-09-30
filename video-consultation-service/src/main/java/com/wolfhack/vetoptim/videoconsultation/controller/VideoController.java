package com.wolfhack.vetoptim.videoconsultation.controller;

import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import com.wolfhack.vetoptim.videoconsultation.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video-sessions")
public class VideoController {

	private final VideoService videoService;

	@PostMapping("/start")
	public ResponseEntity<VideoSession> startSession(@RequestParam Long vetId, @RequestParam Long ownerId) {
		VideoSession session = videoService.startSession(vetId, ownerId);
		return ResponseEntity.ok(session);
	}

	@PostMapping("/end")
	public ResponseEntity<Void> endSession(@RequestParam String sessionId) {
		videoService.endSession(sessionId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/record")
	public ResponseEntity<String> uploadRecording(@RequestParam String sessionId, @RequestPart("file") MultipartFile file) throws IOException {
		File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
		file.transferTo(tempFile);
		String fileUrl = videoService.storeRecording(sessionId, tempFile);
		return ResponseEntity.ok(fileUrl);
	}

	@GetMapping("/{sessionId}/recording")
	public ResponseEntity<String> getVideoRecording(@PathVariable String sessionId) {
		String videoUrl = videoService.getVideoRecording(sessionId);
		return ResponseEntity.ok(videoUrl);
	}

}