package livemarket.like.service;

import livemarket.common.event.EventType;
import livemarket.common.event.payload.ArticleLikedEventPayload;
import livemarket.common.event.payload.ArticleUnlikedEventPayload;
import livemarket.common.outboxmessagerelay.OutboxEventPublisher;
import livemarket.like.entity.ArticleLike;
import livemarket.like.repository.ArticleLikeCountRepository;
import livemarket.like.repository.ArticleLikeRepository;
import livemarket.like.entity.ArticleLikeCount;
import livemarket.like.service.response.ArticleLikeResponse;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }

    @Transactional
    public void like(Long articleId, String memberId) {
        Long userId = Long.parseLong(memberId);

        ArticleLike articleLike = articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));

        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);

        outboxEventPublisher.publish(
                EventType.ARTICLE_LIKED,
                ArticleLikedEventPayload.builder()
                        .articleLikeId(articleLike.getArticleLikeId())
                        .articleId(articleLike.getArticleId())
                        .userId(articleLike.getUserId())
                        .createdAt(articleLike.getCreatedAt())
                        .articleLikeCount(count(articleLike.getArticleId()))
                        .build(),
                articleLike.getArticleId()
        );
    }

    @Transactional
    public void unlike(Long articleId, String memberId) {
        Long userId = Long.parseLong(memberId);

        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    // 좋아요 기록 삭제
                    articleLikeRepository.delete(articleLike);

                    // like_count 감소
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();

                    articleLikeCountRepository.save(articleLikeCount);

                    outboxEventPublisher.publish(
                            EventType.ARTICLE_UNLIKED,
                            ArticleUnlikedEventPayload.builder()
                                    .articleLikeId(articleLike.getArticleLikeId())
                                    .articleId(articleLike.getArticleId())
                                    .userId(articleLike.getUserId())
                                    .createdAt(articleLike.getCreatedAt())
                                    .articleLikeCount(count(articleLike.getArticleId()))
                                    .build(),
                            articleLike.getArticleId()
                    );
                });
    }

    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }

    public boolean isLiked(Long articleId, Long userId) {
        Optional<ArticleLike> articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId);
        return articleLike.isPresent();
    }

    public List<ArticleLikeResponse> getArticleLikeByUserId(Long userId) {
        return articleLikeRepository.findAllByUserId(userId).stream().map(ArticleLikeResponse::from).toList();
    }
}
