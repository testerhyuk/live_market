package livemarket.article.service.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleCreateRequest {
    private String title;
    private String content;
    private int price;
    private String writerId;
    private Long boardId;
    private String category;
}
