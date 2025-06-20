package livemarket.videocall.controller;

import livemarket.videocall.kafka.VideoCallKafkaProducer;
import livemarket.videocall.service.VideoCallService;
import livemarket.videocall.service.dto.VideoCallCreateDto;
import livemarket.videocall.service.dto.VideoCallSessionCreateDto;
import livemarket.videocall.service.dto.request.VideoCallKafkaRequestDto;
import livemarket.videocall.service.dto.response.VideoCallKafkaResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> createSession(@RequestBody VideoCallCreateDto dto) {
        videoCallService.createSessionAndPublish(dto);

        return ResponseEntity.ok().build();
    }
}
