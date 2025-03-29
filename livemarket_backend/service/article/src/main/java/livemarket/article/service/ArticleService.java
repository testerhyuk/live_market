package livemarket.article.service;

import livemarket.article.entity.Article;
import livemarket.article.repository.ArticleRepository;
import livemarket.article.repository.BoardArticleCountRepository;
import livemarket.article.service.request.ArticleCreateRequest;
import livemarket.article.service.request.ArticleUpdateRequest;
import livemarket.article.service.response.ArticlePageResponse;
import livemarket.article.service.response.ArticleResponse;
import livemarket.article.entity.BoardArticleCount;
import livemarket.common.event.EventType;
import livemarket.common.event.payload.ArticleCreatedEventPayload;
import livemarket.common.event.payload.ArticleDeletedEventPayload;
import livemarket.common.event.payload.ArticleUpdatedEventPayload;
import livemarket.common.outboxmessagerelay.OutboxEventPublisher;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final S3Service s3Service;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        List<String> imageUrls = request.getImageUrls();

        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(),
                        request.getBoardId(), request.getWriterId(), imageUrls)
        );

        BoardArticleCount boardArticleCount = boardArticleCountRepository.findLockedByBoardId(request.getBoardId())
                .orElseGet(() -> BoardArticleCount.init(request.getBoardId(), 0L));

        boardArticleCount.increase();
        boardArticleCountRepository.save(boardArticleCount);

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .imageUrls(article.getImageUrls())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        List<String> updatedImages = new ArrayList<>(article.getImageUrls());
        updatedImages.removeAll(request.getDeletedImageUrls());
        updatedImages.addAll(request.getNewImageUrls());

        article.update(request.getTitle(), request.getContent(), updatedImages);

        request.getDeletedImageUrls().forEach(s3Service::deleteImage);

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .imageUrls(article.getImageUrls())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.getImageUrls().forEach(s3Service::deleteImage);

        articleRepository.delete(article);

        boardArticleCountRepository.findLockedByBoardId(article.getBoardId())
                .ifPresent(boardArticleCount -> {
                    boardArticleCount.decrease();
                    boardArticleCountRepository.save(boardArticleCount);
                });

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .imageUrls(article.getImageUrls())
                        .build(),
                article.getBoardId()
        );
    }

    @Transactional(readOnly = true)
    public ArticleResponse read(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(
                () -> new IllegalArgumentException("Article not found")
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    @Transactional
    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);

        return articles.stream().map(ArticleResponse::from).toList();
    }

    @Transactional
    public Long count(Long boardId) {
        return boardArticleCountRepository.findLockedByBoardId(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
