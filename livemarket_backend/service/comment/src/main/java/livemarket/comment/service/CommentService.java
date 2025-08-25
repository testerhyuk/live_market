package livemarket.comment.service;

import livemarket.comment.entity.ArticleCommentCount;
import livemarket.comment.entity.Comment;
import livemarket.comment.repository.ArticleCommentCountRepository;
import livemarket.comment.repository.CommentRepository;
import livemarket.comment.service.request.CommentCreateRequest;
import livemarket.comment.service.request.CommentUpdateRequest;
import livemarket.comment.service.response.CommentPageResponse;
import livemarket.comment.service.response.CommentResponse;
import livemarket.common.event.EventType;
import livemarket.common.event.payload.CommentCreatedEventPayload;
import livemarket.common.event.payload.CommentDeletedEventPayload;
import livemarket.common.outboxmessagerelay.OutboxEventPublisher;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final Snowflake snowflake = new Snowflake();
    private final ArticleCommentCountRepository articleCommentCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public CommentResponse create(CommentCreateRequest request, String memberId) {
        Long writerId = Long.parseLong(memberId);

        Comment parent = findParent(request);

        Comment comment = commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        request.getContent(),
                        parent == null ? null : parent.getCommentId(),
                        Long.valueOf(request.getArticleId()),
                        writerId
                )
        );

        int result = articleCommentCountRepository.increase(Long.valueOf(request.getArticleId()));
        if (result == 0) {
            articleCommentCountRepository.save(
                    ArticleCommentCount.init(Long.valueOf(request.getArticleId()), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.COMMENT_CREATED,
                CommentCreatedEventPayload.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .articleId(comment.getArticleId())
                        .writerId(comment.getWriterId())
                        .deleted(comment.getDeleted())
                        .createdAt(comment.getCreatedAt())
                        .articleCommentCount(count(comment.getArticleId()))
                        .build(),
                comment.getArticleId()
        );

        return CommentResponse.from(comment);
    }

    private Comment findParent(CommentCreateRequest request) {

        if (request.getParentCommentId() == null) {
            return null;
        }

        Long parentCommentId = Long.valueOf(request.getParentCommentId());

        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId, Long userId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> !c.getDeleted())
                .filter(c -> c.getWriterId().equals(userId))
                .orElseThrow(() -> new AccessDeniedException("삭제할 권한이 없거나 이미 삭제된 댓글입니다."));

        if (hasChildren(comment)) {
            comment.delete();
        } else {
            delete(comment);
        }

        outboxEventPublisher.publish(
                EventType.COMMENT_DELETED,
                CommentDeletedEventPayload.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .articleId(comment.getArticleId())
                        .writerId(comment.getWriterId())
                        .deleted(comment.getDeleted())
                        .createdAt(comment.getCreatedAt())
                        .articleCommentCount(count(comment.getArticleId()))
                        .build(),
                comment.getArticleId()
        );
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(Comment comment) {
        commentRepository.delete(comment);

        articleCommentCountRepository.decrease(comment.getArticleId());

        if(!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                    .filter(Comment::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }

    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
        return CommentPageResponse.of(
                commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize).stream()
                        .map(CommentResponse::from)
                        .toList(),
                commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        );
    }

    // 무한 스크롤
    public List<CommentResponse> readAll(Long articleId, Long lastParentCommentId, Long lastCommentId, Long limit) {
        List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
                commentRepository.findAllInfiniteScroll(articleId, limit) :
                commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    public Long count(Long boardId) {
        return articleCommentCountRepository.findById(boardId)
                .map(ArticleCommentCount::getCommentCount)
                .orElse(0L);
    }

    @Transactional
    public CommentResponse update(Long commentId, CommentUpdateRequest request, Long userId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        if (!comment.getWriterId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        comment.update(request.getComment());

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getCommentByUserId(Long userId) {
        return commentRepository.findAllByWriterId(userId).stream().map(CommentResponse::from).toList();
    }
}
