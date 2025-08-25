package livemarket.videocall.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.videocall.service.dto.VideoCallNotificationDto;
import livemarket.videocall.service.dto.request.VideoCallKafkaRequestDto;
import livemarket.videocall.service.dto.response.VideoCallKafkaResponseDto;
import livemarket.videocall.service.dto.response.VideoCallSessionCreatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoCallKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCallRequest(VideoCallKafkaRequestDto dto) {
        send("video-call.request", dto);
    }

    public void sendCallResponse(VideoCallKafkaResponseDto dto) {
        send("video-call.response", dto);
    }

    public void sendSessionCreated(VideoCallSessionCreatedDto dto) {
        send("video-call.session", dto);
    }

    public void sendNotification(VideoCallNotificationDto dto) {
        send("video-call.notify", dto);
    }

    private void send(String topic, Object dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }
}
