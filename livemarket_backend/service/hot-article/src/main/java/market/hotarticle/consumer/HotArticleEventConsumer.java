package market.hotarticle.consumer;

import market.hotarticle.service.HotArticleService;
import livemarket.common.event.Event;
import livemarket.common.event.EventPayload;
import livemarket.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotArticleEventConsumer {
    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.LIVEMARKET_ARTICLE,
            EventType.Topic.LIVEMARKET_COMMENT,
            EventType.Topic.LIVEMARKET_LIKE,
            EventType.Topic.LIVEMARKET_VIEW
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received message = {}", message);

        Event<EventPayload> event = Event.fromJson(message);

        if(event != null) {
            hotArticleService.handleEvent(event);
        }

        ack.acknowledge();
    }
}
