package livemarket.videocall.event;

import livemarket.common.event.Event;
import livemarket.common.event.EventPayload;
import livemarket.common.event.EventType;
import livemarket.common.event.payload.VideoCallSessionCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoCallSessionCreatedEventListener {
    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REDIS_CHANNEL = "video-call-notify";

    @KafkaListener(topics = EventType.Topic.LIVEMARKET_VIDEOCALL, groupId = "video-call-group")
    public void handle(String message) {
        log.info("[KafkaListener] Received message: {}", message);
        Event<EventPayload> event = Event.fromJson(message);

        if (event == null) {
            log.error("[VideoCallSessionCreatedEventListener] Invalid message: {}", message);
            return;
        }


        if (event.getPayload() instanceof VideoCallSessionCreatedPayload payload) {
            messagingTemplate.convertAndSend(
                    "/topic/video-calls/" + payload.getReceiverId(),
                    payload
            );
            log.info("STOMP 알림 발송 완료 to {}", payload.getReceiverId());
        }

        redisTemplate.convertAndSend(REDIS_CHANNEL, message);
    }
}
