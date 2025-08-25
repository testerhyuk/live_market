package livemarket.articleImages.controller;

import livemarket.articleImages.service.ArticleImagesService;
import livemarket.articleImages.service.S3Service;
import livemarket.articleImages.service.request.ArticleImagesUpdateRequest;
import livemarket.articleImages.service.request.ArticleImagesUploadRequest;
import livemarket.articleImages.service.response.ArticleImagesResponse;
import livemarket.articleImages.service.response.PreSignedUrlListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ArticleImagesController {
    private final S3Service s3Service;
    private final ArticleImagesService articleImagesService;

    @PostMapping("/v1/article-images/presigned-urls")
    public PreSignedUrlListResponse getPreSignedUrls(@RequestBody List<String> filenames) {
        return new PreSignedUrlListResponse(s3Service.createPreSignedUrls(filenames));
    }

    @PostMapping("/v1/article-images")
    public List<ArticleImagesResponse> upload(@RequestBody ArticleImagesUploadRequest request) {
        log.info("Request : {}", request);
        return articleImagesService.uploadImages(request);
    }

    @PutMapping("/v1/article-images")
    public List<ArticleImagesResponse> update(@RequestBody ArticleImagesUpdateRequest request) {
        return articleImagesService.updateImages(request);
    }

    @GetMapping("/v1/article-images/article/{articleId}")
    public List<String> getArticleImages(@PathVariable("articleId") String articleId) {
        return articleImagesService.getImageUrlsByArticleId(Long.valueOf(articleId));
    }

    @DeleteMapping("/v1/article-images/article/{articleId}")
    public void delete(@PathVariable("articleId") String articleId, @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        articleImagesService.delete(Long.parseLong(articleId), Long.parseLong(userId));
    }
}
