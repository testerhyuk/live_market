package livemarket.common.event;

import livemarket.common.event.payload.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.LIVEMARKET_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.LIVEMARKET_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.LIVEMARKET_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.LIVEMARKET_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.LIVEMARKET_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload.class, Topic.LIVEMARKET_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload.class, Topic.LIVEMARKET_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload.class, Topic.LIVEMARKET_VIEW)
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type = {}", type, e);

            return null;
        }
    }

    public static class Topic {
        public static final String LIVEMARKET_ARTICLE = "livemarket-article";
        public static final String LIVEMARKET_COMMENT = "livemarket-comment";
        public static final String LIVEMARKET_LIKE = "livemarket-like";
        public static final String LIVEMARKET_VIEW = "livemarket-view";
    }
}
