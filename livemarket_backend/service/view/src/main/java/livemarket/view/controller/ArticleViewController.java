package livemarket.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import livemarket.view.service.ArticleViewService;

@RestController
@RequiredArgsConstructor
public class ArticleViewController {
    private final ArticleViewService articleViewService;

    @PostMapping("/v1/article-views/articles/{articleId}")
    public Long increase(
            @PathVariable("articleId") Long articleId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return  articleViewService.increase(articleId, userId);
    }

    @GetMapping("/v1/article-views/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return articleViewService.count(articleId);
    }
}
