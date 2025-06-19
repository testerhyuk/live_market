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
                            request.getArticleId(),
                            request.getUserId(),
                            url
                    );
                    articleImagesRepository.save(articleImage);

                    return ArticleImagesResponse.from(articleImage);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ArticleImagesResponse> updateImages(ArticleImagesUpdateRequest request) {
        Long articleId = request.getArticleId();
        Long userId = request.getUserId();

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
                .filter(img -> !img.isDeleted())
                .filter(img -> remaining.contains(img.getArticleImageUrl()))
                .map(ArticleImagesResponse::from)
                .collect(Collectors.toList());

        remainingResponses.addAll(newSaved);

        return remainingResponses;
    }
}
