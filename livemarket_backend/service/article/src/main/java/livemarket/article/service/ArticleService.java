package livemarket.article.service;

import livemarket.article.entity.Article;
import livemarket.article.entity.Category;
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

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request, String memberId) {
        Long writerId = Long.parseLong(memberId);

        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getPrice(),
                        request.getBoardId(), writerId, Category.valueOf(request.getCategory()))
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
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(request.getTitle(), request.getContent(), request.getPrice(), Category.valueOf(request.getCategory()));

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
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public void delete(Long articleId, Long userId) throws AccessDeniedException {
        Article article = articleRepository.findById(articleId).orElseThrow();

        if (!article.getWriterId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다");
        }

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

    public List<ArticleResponse> search(Long boardId, String keyword, Long limit, Long lastArticleId) {
        String keywordForDB = "%" + keyword + "%";

        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllByTitleAndContentContaining(boardId, keywordForDB, limit) :
                articleRepository.findAllByTitleAndContentContaining(boardId, keywordForDB, limit, lastArticleId);

        return articles.stream().map(ArticleResponse::from).toList();
    }

    public List<ArticleResponse> readAllByCategory(Long boardId, String category, Long limit, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllByCategory(boardId, category, limit) :
                articleRepository.findAllByCategory(boardId, category, limit, lastArticleId);

        return articles.stream().map(ArticleResponse::from).toList();
    }

    public List<ArticleResponse> readAllByWriterId(String userId) {
        Long memberId = Long.parseLong(userId);

        return articleRepository.findAllByWriterId(memberId).stream().map(ArticleResponse::from).toList();
    }
}
