package livemarket.videocall.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.videocall.service.dto.VideoCallNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoCallNotificationSender {
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void sendNotification(VideoCallNotificationDto dto) {
        log.info("알림 전송 : {} -> {}", dto.getFromMemberId(), dto.getToMemberId());

        messagingTemplate.convertAndSend(
                "/topic/video-call/" + dto.getToMemberId(),
                dto
        );

        try {
            String channel = "video-call-notify";
            String message = objectMapper.writeValueAsString(dto);  // DTO -> JSON 문자열 변환
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("Redis 메시지 발행 실패", e);
        }
    }
}
