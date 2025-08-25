package livemarket.comment.service.response;

import livemarket.comment.entity.Comment;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private String commentId;
    private String content;
    private String parentCommentId;
    private String articleId;
    private String writerId;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = String.valueOf(comment.getCommentId());
        response.content = comment.getContent();
        response.parentCommentId = String.valueOf(comment.getParentCommentId());
        response.articleId = String.valueOf(comment.getArticleId());
        response.writerId = String.valueOf(comment.getWriterId());
        response.deleted = comment.getDeleted();
        response.createdAt = comment.getCreatedAt();
        return response;
    }
}
