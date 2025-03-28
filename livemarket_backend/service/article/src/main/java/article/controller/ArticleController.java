package article.controller;

import article.service.ArticleService;
import article.service.S3Service;
import article.service.request.ArticleCreateRequest;
import article.service.request.ArticleUpdateRequest;
import article.service.response.ArticlePageResponse;
import article.service.response.ArticleResponse;
import article.service.response.PreSignedUrlListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final S3Service s3Service;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(@PathVariable("articleId") Long articleId) {
        return articleService.read(articleId);
    }

    @PostMapping("/v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @PutMapping("/v1/articles/{articleId}")
    public ArticleResponse update(@PathVariable("articleId") Long articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(@PathVariable("articleId") Long articleId) {
        articleService.delete(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse readAll(
            @RequestParam("boardId") Long boardId,
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize
    ) {
        return articleService.readAll(boardId, page, pageSize);
    }

    @GetMapping("/v1/articles/infinite-scroll")
    public List<ArticleResponse> readAllInfiniteScroll(
            @RequestParam("boardId") Long boardId,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
    ) {
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }

    @GetMapping("/v1/articles/boards/{boardId}/count")
    public Long count(@PathVariable("boardId") Long boardId) {
        return articleService.count(boardId);
    }

    @PostMapping("/presigned-urls")
    public PreSignedUrlListResponse getPreSignedUrls(@RequestBody List<String> fileNames) {
        return new PreSignedUrlListResponse(s3Service.createPreSignedUrls(fileNames));
    }
}
