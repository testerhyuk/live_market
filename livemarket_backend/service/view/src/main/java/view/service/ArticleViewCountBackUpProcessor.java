package view.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import view.entity.ArticleViewCount;
import view.repository.ArticleViewCountBackUpRepository;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {
    private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);

        if(result == 0) {
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> { },
                            () -> articleViewCountBackUpRepository.save(
                                    ArticleViewCount.init(articleId, viewCount)
                            ));
        }
    }
}
