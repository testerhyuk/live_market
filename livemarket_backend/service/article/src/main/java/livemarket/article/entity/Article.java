package livemarket.article.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    @Enumerated(EnumType.STRING)
    private Category category;
    private int price;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Article create(Long articleId, String title, String content, int price,
                                 Long boardId, Long writerId, Category category) {
        Article article = new Article();

        article.articleId = articleId;
        article.title = title;
        article.content = content;
        article.price = price;
        article.boardId = boardId;
        article.writerId = writerId;
        article.createdAt = LocalDateTime.now();
        article.modifiedAt = article.createdAt;
        article.category = category;

        return article;
    }

    public void update(String title, String content, int price, Category category) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.category = category;
        modifiedAt = LocalDateTime.now();
    }
}
