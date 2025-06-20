package livemarket.common.outboxmessagerelay;

import livemarket.common.event.Event;
import livemarket.common.event.EventPayload;
import livemarket.common.event.EventType;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutboxEventPublisher {
    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, Long shardKey) {
        log.info("[OutboxEventPublisher] publish called with type={}, payload={}, shardKey={}", type, payload, shardKey);
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                Event.of(
                        eventIdSnowflake.nextId(), type, payload
                ).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
        log.info("[OutboxEventPublisher] Published event to outbox: type={}, payload={}", type, payload);
    }
}
