package livemarket.articleImages.repository;

import livemarket.articleImages.entity.ArticleImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleImagesRepository extends JpaRepository<ArticleImages, Long> {
    List<ArticleImages> findByArticleId(Long articleId);
    List<ArticleImages> findAllByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoff);
}
