package livemarket.articleImages.service;

import livemarket.articleImages.entity.ArticleImages;
import livemarket.articleImages.repository.ArticleImagesRepository;
import livemarket.articleImages.service.request.ArticleImagesUpdateRequest;
import livemarket.articleImages.service.request.ArticleImagesUploadRequest;
import livemarket.articleImages.service.response.ArticleImagesResponse;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleImagesService {
    private final ArticleImagesRepository articleImagesRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public List<ArticleImagesResponse> uploadImages(ArticleImagesUploadRequest request) {
        return request.getImageUrls().stream()
                .map(url -> {
                    ArticleImages articleImage = ArticleImages.upload(
                            snowflake.nextId(),
                            Long.parseLong(request.getArticleId()),
                            Long.parseLong(request.getUserId()),
                            url
                    );
                    articleImagesRepository.save(articleImage);

                    return ArticleImagesResponse.from(articleImage);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ArticleImagesResponse> updateImages(ArticleImagesUpdateRequest request) {
        Long articleId = Long.valueOf(request.getArticleId());
        Long userId = Long.valueOf(request.getUserId());

        List<ArticleImages> currentImages = articleImagesRepository.findByArticleId(articleId);

        List<String> remaining = request.getRemainingImageIds();

        // 삭제할 이미지
        List<ArticleImages> toDelete = currentImages.stream()
                .filter(img -> !remaining.contains(img.getArticleImageUrl()))
                .toList();

        // 삭제 처리
        toDelete.forEach(img -> {
            img.softDelete();
            articleImagesRepository.save(img);
        });

        // 새로 추가할 이미지 저장
        List<ArticleImagesResponse> newSaved = request.getNewImageUrls().stream()
                .map(url -> {
                    ArticleImages articleImages = ArticleImages.upload(
                            snowflake.nextId(),
                            articleId,
                            userId,
                            url
                    );
                    articleImagesRepository.save(articleImages);

                    return ArticleImagesResponse.from(articleImages);
                })
                .toList();

        // 기존 이미지 + 새로 추가된 이미지 반환
        List<ArticleImagesResponse> remainingResponses = currentImages.stream()
                .filter(img -> remaining.contains(img.getArticleImageUrl()))
                .map(ArticleImagesResponse::from)
                .collect(Collectors.toList());

        remainingResponses.addAll(newSaved);

        return remainingResponses;
    }

    public List<String> getImageUrlsByArticleId(Long articleId) {
        return articleImagesRepository.findByArticleId(articleId).stream()
                .map(ArticleImages::getArticleImageUrl)
                .toList();
    }

    public void delete(Long articleId, Long userId) throws AccessDeniedException {
        List<ArticleImages> images = articleImagesRepository.findByArticleId(articleId);
        boolean isOwner = images.stream().allMatch(img -> img.getUserId().equals(userId));

        if (!isOwner) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        images.forEach(img -> {
            img.softDelete();
            articleImagesRepository.save(img);
        });
    }
}
