package article.api;

import article.service.response.ArticlePageResponse;
import article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));

        log.info("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(157382179826307072L);
        log.info("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        update(157382179826307072L);
        ArticleResponse response = read(157382179826307072L);
        log.info("response = " + response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi2", "my content2"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 157382179826307072L)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        log.info("response.getArticleCount() = " + response.getArticleCount());

        for (ArticleResponse article : response.getArticles()) {
            log.info("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> articles1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        log.info("firstPage");

        for (ArticleResponse articleResponse : articles1) {
            log.info("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }

        Long lastArticleId = articles1.getLast().getArticleId();

        List<ArticleResponse> articles2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        log.info("secondPage");

        for (ArticleResponse articleResponse : articles2) {
            log.info("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequest("hi", "content", 1L, 8L));

        Long count1 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 8L)
                .retrieve()
                .body(Long.class);

        log.info("count1 = " + count1);

        restClient.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve()
                .body(ArticleResponse.class);

        Long count2 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 8L)
                .retrieve()
                .body(Long.class);

        log.info("count2 = " + count2);
    }

    @Test
    void ConcurrencyCountTest() throws InterruptedException {
        Long boardId = 8L;
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long writerId = (long) (300 + i);
            executorService.execute(() -> {
                try {
                    create(new ArticleCreateRequest("동시성 테스트", "내용", writerId, boardId));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결과 검증
        Long articleCount = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", boardId)
                .retrieve()
                .body(Long.class);

        log.info("최종 articleCount = " + articleCount);
        assertThat(articleCount).isEqualTo(threadCount);
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
