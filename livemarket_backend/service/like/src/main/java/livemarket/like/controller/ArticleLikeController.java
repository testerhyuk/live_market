package livemarket.like.controller;

import livemarket.like.service.ArticleLikeService;
import livemarket.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/v1/article-likes/articles/{articleId}/status")
    public Map<String, Boolean> getLikeStatus(
            @PathVariable("articleId") Long articleId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        boolean likeStatus = articleLikeService.isLiked(articleId, userId);

        return Collections.singletonMap("likeStatus", likeStatus);
    }

    @GetMapping("/v1/article-likes/articles/member/{userId}")
    public List<ArticleLikeResponse> getArticleLikeByUserId(@PathVariable("userId") String userId) {
        Long memberId = Long.parseLong(userId);
        return articleLikeService.getArticleLikeByUserId(memberId);
    }
}
