package livemarket.videocall.controller;

import livemarket.videocall.kafka.VideoCallKafkaProducer;
import livemarket.videocall.service.VideoCallService;
import livemarket.videocall.service.dto.OpenViduSessionResult;
import livemarket.videocall.service.dto.VideoCallCreateDto;
import livemarket.videocall.service.dto.VideoCallSessionCreateDto;
import livemarket.videocall.service.dto.request.VideoCallKafkaRequestDto;
import livemarket.videocall.service.dto.response.VideoCallKafkaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VideoCallController {
    private final VideoCallKafkaProducer kafkaProducer;
    private final VideoCallService videoCallService;

    @PostMapping("/v1/video-calls/request")
    public ResponseEntity<Void> sendCallRequest(@RequestBody VideoCallKafkaRequestDto request) {
        kafkaProducer.sendCallRequest(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/v1/video-calls/response")
    public ResponseEntity<Void> sendCallResponse(@RequestBody VideoCallKafkaResponseDto response) {
        kafkaProducer.sendCallResponse(response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/v1/video-calls/sessions")
    public ResponseEntity<OpenViduSessionResult> createSession(@RequestBody VideoCallCreateDto dto) {
        OpenViduSessionResult sessionResult = videoCallService.createSessionAndPublish(dto);

        return ResponseEntity.ok(sessionResult);
    }

    @PostMapping("/v1/video-calls/sessions/join")
    public ResponseEntity<OpenViduSessionResult> joinSession(@RequestBody VideoCallSessionCreateDto dto) {
        OpenViduSessionResult result = videoCallService.joinSession(dto);
        return ResponseEntity.ok(result);
    }
}
