package livemarket.articleImages.service.response;

import livemarket.articleImages.entity.ArticleImages;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleImagesResponse {
    private Long articleImageId;
    private Long articleId;
    private Long userId;
    private String articleImageUrl;

    public static ArticleImagesResponse from(ArticleImages articleImages) {
        ArticleImagesResponse response = new ArticleImagesResponse();

        response.articleImageId = articleImages.getArticleImagesId();
        response.articleId = articleImages.getArticleId();
        response.userId = articleImages.getUserId();
        response.articleImageUrl = articleImages.getArticleImageUrl();

        return response;
    }
}
