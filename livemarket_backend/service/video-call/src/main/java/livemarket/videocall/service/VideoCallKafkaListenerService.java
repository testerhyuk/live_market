package livemarket.videocall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.videocall.service.dto.VideoCallEventWrapper;
import livemarket.videocall.service.dto.VideoCallNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Log4j2
@RequiredArgsConstructor
public class VideoCallKafkaListenerService {
    private final ObjectMapper objectMapper;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void registerEmitter(String memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);

        try {
            emitter.send(SseEmitter.event().name("INIT").data("connected"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
    }

    @KafkaListener(topics = "video-call.notify")
    public void listen(String message) {
        try {
            VideoCallNotificationDto dto = objectMapper.readValue(message, VideoCallNotificationDto.class);
            log.info("수신된 DTO: {}", dto);

            // SSE 전송
            SseEmitter emitter = emitters.get(dto.getToMemberId());
            if (emitter != null) {
                emitter.send(SseEmitter.event()
                        .name("VIDEO_CALL")
                        .data(dto)
                );
                log.info("SSE 전송 완료: {}", dto.getToMemberId());
            } else {
                log.warn("SSE Emitter 없음, 수신자: {}", dto.getToMemberId());
            }
        } catch (Exception e) {
            log.error("Kafka 메시지 파싱 또는 SSE 전송 오류", e);
        }
    }
}
