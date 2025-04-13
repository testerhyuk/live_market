package livemarket.like.controller;

import livemarket.like.service.ArticleLikeService;
import livemarket.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {
    private final ArticleLikeService articleLikeService;

    @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(@PathVariable("articleId") Long articleId, @PathVariable("userId") Long userId) {
        return articleLikeService.read(articleId, userId);
    }

    @PostMapping("/v1/article-likes/articles/{articleId}")
    public void like(@PathVariable("articleId") Long articleId, @RequestHeader("X-User-Id") String memberId) {
        articleLikeService.like(articleId, memberId);
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}")
    public void unlike(@PathVariable("articleId") Long articleId, @RequestHeader("X-User-Id") String memberId) {
        articleLikeService.unlike(articleId, memberId);
    }

    @GetMapping("/v1/article-likes/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return articleLikeService.count(articleId);
    }
}
