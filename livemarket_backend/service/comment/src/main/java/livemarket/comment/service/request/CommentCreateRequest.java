package livemarket.comment.service.request;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private String articleId;
    private String content;
    private String parentCommentId;
    private String writerId;
}
