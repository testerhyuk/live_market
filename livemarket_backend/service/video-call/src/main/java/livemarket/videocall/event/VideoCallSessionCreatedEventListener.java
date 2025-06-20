package livemarket.videocall.event;

import livemarket.common.event.Event;
import livemarket.common.event.EventPayload;
import livemarket.common.event.EventType;
import livemarket.common.event.payload.VideoCallSessionCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoCallSessionCreatedEventListener {
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_CHANNEL = "video-call-notify";

    @KafkaListener(topics = EventType.Topic.LIVEMARKET_VIDEOCALL)
    public void handle(String message) {
        log.info("[KafkaListener] Received message: {}", message);
        Event<EventPayload> event = Event.fromJson(message);

        if (event == null) {
            log.error("[VideoCallSessionCreatedEventListener] Invalid message: {}", message);
            return;
        }

        if (!(event.getPayload() instanceof VideoCallSessionCreatedPayload payload)) {
            log.error("[VideoCallSessionCreatedEventListener] Unexpected payload type: {}",
                    event.getPayload().getClass());
            return;
        }

        log.info("[VideoCallSessionCreatedEventListener] Sending notify to receiverId={}, sessionId={}",
                payload.getReceiverId(), payload.getSessionId());

        redisTemplate.convertAndSend(REDIS_CHANNEL, message);
    }
}
