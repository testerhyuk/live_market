package livemarket.articleImages.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleImages {
    @Id
    private Long articleImagesId;
    private Long articleId;
    private Long userId;
    @Column(length = 2000)
    private String articleImageUrl;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ArticleImages upload(Long articleImagesId, Long articleId, Long userId, String articleImageUrl) {
        ArticleImages articleImages = new ArticleImages();

        articleImages.articleImagesId = articleImagesId;
        articleImages.articleId = articleId;
        articleImages.userId = userId;
        articleImages.articleImageUrl = articleImageUrl;
        articleImages.createdAt = LocalDateTime.now();
        articleImages.updatedAt = articleImages.createdAt;

        return articleImages;
    }

    public void update(String articleImagesUrl) {
        this.articleImageUrl = articleImagesUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
