package livemarket.videocall.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.videocall.service.VideoCallService;
import livemarket.videocall.service.dto.request.VideoCallKafkaRequestDto;
import livemarket.videocall.service.dto.response.VideoCallKafkaResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoCallKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final VideoCallService videoCallService;

    @KafkaListener(topics = "video-call.request", groupId = "videocall-group")
    public void handleRequest(String message) {
        try {
            VideoCallKafkaRequestDto dto = objectMapper.readValue(message, VideoCallKafkaRequestDto.class);
            log.info("화상채팅 요청 수신 : {}", dto);
            videoCallService.handleRequest(dto);
        } catch (Exception e) {
            log.error("Kafka 요청 처리 실패", e);
        }
    }

    @KafkaListener(topics = "video-call.response", groupId = "videocall-group")
    public void handleResponse(String message) {
        try {
            VideoCallKafkaResponseDto dto = objectMapper.readValue(message, VideoCallKafkaResponseDto.class);
            log.info("화상채팅 응답 수신 : {}", dto);
            videoCallService.handleResponse(dto);
        } catch (Exception e) {
            log.error("Kafka 응답 처리 실패", e);
        }
    }
}
