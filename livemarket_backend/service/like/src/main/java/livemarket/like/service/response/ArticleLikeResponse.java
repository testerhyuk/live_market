package livemarket.like.service.response;

import livemarket.like.entity.ArticleLike;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleLikeResponse {
    private String articleLikeId;
    private String articleId;
    private String userId;
    private LocalDateTime createdAt;

    public static ArticleLikeResponse from(ArticleLike articleLike) {
        ArticleLikeResponse response = new ArticleLikeResponse();
        response.articleLikeId = String.valueOf(articleLike.getArticleLikeId());
        response.articleId = String.valueOf(articleLike.getArticleId());
        response.userId = String.valueOf(articleLike.getUserId());
        response.createdAt = articleLike.getCreatedAt();
        return response;
    }
}
