package article.service.response;

import article.entity.Article;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ArticleResponse {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<String> imageUrls;

    // Article 타입 -> ArticleResponse 타입으로 변환
    public static ArticleResponse from(Article article) {
        ArticleResponse response = new ArticleResponse();

        response.articleId = article.getArticleId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.boardId = article.getBoardId();
        response.writerId = article.getWriterId();
        response.createdAt = article.getCreatedAt();
        response.modifiedAt = article.getModifiedAt();
        response.imageUrls = new ArrayList<>(article.getImageUrls());

        return response;
    }
}
