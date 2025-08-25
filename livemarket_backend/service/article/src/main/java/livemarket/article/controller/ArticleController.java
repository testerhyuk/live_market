package livemarket.article.controller;

import livemarket.article.service.request.ArticleCreateRequest;
import livemarket.article.service.request.ArticleUpdateRequest;
import livemarket.article.service.response.ArticlePageResponse;
import livemarket.article.service.response.ArticleResponse;
import livemarket.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(@PathVariable("articleId") String articleId) {
        return articleService.read(Long.parseLong(articleId));
    }

    @PostMapping("/v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request,
                                  @RequestHeader("X-User-Id") String memberId) {
        return articleService.create(request, memberId);
    }

    @PutMapping("/v1/articles/{articleId}")
    public ArticleResponse update(@PathVariable("articleId") String articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(Long.valueOf(articleId), request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(@PathVariable("articleId") String articleId, @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        articleService.delete(Long.valueOf(articleId), Long.valueOf(userId));
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

    @GetMapping("/v1/articles/category")
    public List<ArticleResponse> readAllByCategory(@RequestParam("boardId") Long boardId,
                                                   @RequestParam("category") String category,
                                                   @RequestParam("pageSize") Long pageSize,
                                                   @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
    ) {
        return articleService.readAllByCategory(boardId, category, pageSize, lastArticleId);
    }

    @GetMapping("/v1/articles/search")
    public List<ArticleResponse> search(
            @RequestParam("boardId") Long boardId,
            @RequestParam("keyword") String keyword,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "lastArticleId", required = false) Long lastArticleId) {

        return articleService.search(boardId, keyword, pageSize, lastArticleId);
    }

    @GetMapping("/v1/articles/member/{userId}")
    public List<ArticleResponse> getListByUserId(@PathVariable("userId") String userId) {
        return articleService.readAllByWriterId(userId);
    }
}
