package livemarket.article.service.response;

import livemarket.article.entity.Article;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleResponse {
    private String articleId;
    private String title;
    private String content;
    private Long boardId;
    private String writerId;
    private String category;
    private int price;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // Article 타입 -> ArticleResponse 타입으로 변환
    public static ArticleResponse from(Article article) {
        ArticleResponse response = new ArticleResponse();

        response.articleId = String.valueOf(article.getArticleId());
        response.title = article.getTitle();
        response.content = article.getContent();
        response.boardId = article.getBoardId();
        response.writerId = String.valueOf(article.getWriterId());
        response.createdAt = article.getCreatedAt();
        response.modifiedAt = article.getModifiedAt();
        response.category = String.valueOf(article.getCategory());
        response.price = article.getPrice();

        return response;
    }
}
