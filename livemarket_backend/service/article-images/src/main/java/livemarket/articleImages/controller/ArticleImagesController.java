package livemarket.articleImages.controller;

import livemarket.articleImages.service.ArticleImagesService;
import livemarket.articleImages.service.S3Service;
import livemarket.articleImages.service.request.ArticleImagesUpdateRequest;
import livemarket.articleImages.service.request.ArticleImagesUploadRequest;
import livemarket.articleImages.service.response.ArticleImagesResponse;
import livemarket.articleImages.service.response.PreSignedUrlListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleImagesController {
    private final S3Service s3Service;
    private final ArticleImagesService articleImagesService;

    @PostMapping("/presigned-urls")
    public PreSignedUrlListResponse getPreSignedUrls(@RequestBody List<String> filenames) {
        return new PreSignedUrlListResponse(s3Service.createPreSignedUrls(filenames));
    }

    @PostMapping("/v1/article-images")
    public List<ArticleImagesResponse> upload(@RequestBody ArticleImagesUploadRequest request) {
        return articleImagesService.uploadImages(request);
    }

    @PutMapping("/v1/article-images")
    public List<ArticleImagesResponse> update(@RequestBody ArticleImagesUpdateRequest request) {
        return articleImagesService.updateImages(request);
    }
}
