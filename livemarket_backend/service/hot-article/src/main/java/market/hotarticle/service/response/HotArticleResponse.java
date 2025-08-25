package market.hotarticle.service.response;

import lombok.ToString;
import market.hotarticle.client.ArticleClient;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ToString
public class HotArticleResponse {
    private String articleId;
    private String title;
    private int price;
    private LocalDateTime createdAt;

    public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
        HotArticleResponse response = new HotArticleResponse();

        response.articleId = String.valueOf(articleResponse.getArticleId());
        response.title = articleResponse.getTitle();
        response.price = articleResponse.getPrice();
        response.createdAt = articleResponse.getCreatedAt();

        return response;
    }
}
