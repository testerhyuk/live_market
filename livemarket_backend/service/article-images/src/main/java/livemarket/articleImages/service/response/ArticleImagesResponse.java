package livemarket.articleImages.service.response;

import livemarket.articleImages.entity.ArticleImages;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleImagesResponse {
    private String articleImageId;
    private String articleId;
    private String userId;
    private String articleImageUrl;

    public static ArticleImagesResponse from(ArticleImages articleImages) {
        ArticleImagesResponse response = new ArticleImagesResponse();

        response.articleImageId = String.valueOf(articleImages.getArticleImagesId());
        response.articleId = String.valueOf(articleImages.getArticleId());
        response.userId = String.valueOf(articleImages.getUserId());
        response.articleImageUrl = articleImages.getArticleImageUrl();

        return response;
    }
}
