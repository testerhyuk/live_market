package article.entity;

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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    @ElementCollection
    @CollectionTable(name = "article_image_urls", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "image_urls", length = 2000)
    private List<String> imageUrls = new ArrayList<>();

    public static Article create(Long articleId, String title, String content,
                                 Long boardId, Long writerId, List<String> imageUrls) {
        Article article = new Article();

        article.articleId = articleId;
        article.title = title;
        article.content = content;
        article.boardId = boardId;
        article.writerId = writerId;
        article.createdAt = LocalDateTime.now();
        article.modifiedAt = article.createdAt;
        article.imageUrls = imageUrls;

        return article;
    }

    public void update(String title, String content, List<String> newImageUrls) {
        this.title = title;
        this.content = content;

        this.imageUrls.clear();
        this.imageUrls.addAll(newImageUrls);

        modifiedAt = LocalDateTime.now();
    }
}
