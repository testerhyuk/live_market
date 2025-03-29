package market.hotarticle.service.eventhandler;

import livemarket.common.event.Event;
import livemarket.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findArticleId(Event<T> event);
}
