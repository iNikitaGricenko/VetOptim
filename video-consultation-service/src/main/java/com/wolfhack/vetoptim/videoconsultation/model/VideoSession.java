package com.wolfhack.vetoptim.videoconsultation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "video_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSession {

    @Id
    private String id;
    private Long veterinarianId;
    private Long petOwnerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean recorded;
    private String videoUrl;

    private String transcodingJobId;

}