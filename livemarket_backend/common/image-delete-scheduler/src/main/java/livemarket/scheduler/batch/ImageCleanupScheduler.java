package livemarket.scheduler.batch;

import livemarket.articleImages.entity.ArticleImages;
import livemarket.articleImages.repository.ArticleImagesRepository;
import livemarket.articleImages.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {
    private final ArticleImagesRepository articleImagesRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupDeletedImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(100);

        List<ArticleImages> imagesToHardDelete = articleImagesRepository.findAllByDeletedTrueAndDeletedAtBefore(cutoff);

        imagesToHardDelete.forEach(image -> {
            s3Service.deleteImage(image.getArticleImageUrl());
            articleImagesRepository.delete(image);
        });
    }
}
