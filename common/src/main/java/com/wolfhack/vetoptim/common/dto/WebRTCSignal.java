package com.wolfhack.vetoptim.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCSignal {
    private String type;
    private String sdp;
    private String candidate;
    private String targetSessionId;
    private Long vetId;
    private Long ownerId;
    private String sessionId;
}