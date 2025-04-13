package like.api;

import livemarket.like.service.response.ArticleLikeResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class LikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");
    RestClient gatewayRestClient = RestClient.create("http://localhost:9007");

    @Test
    void likeAndUnlikeTest() {
        Long articleId = 157390014073294857L;

        like(articleId, 1L);
        like(articleId, 2L);
        like(articleId, 3L);

        ArticleLikeResponse response1 = read(articleId, 1L);
        ArticleLikeResponse response2 = read(articleId, 2L);
        ArticleLikeResponse response3 = read(articleId, 3L);
        System.out.println("response1 = " + response1);
        System.out.println("response2 = " + response2);
        System.out.println("response3 = " + response3);

        unlike(articleId, 1L);
        unlike(articleId, 2L);
        unlike(articleId, 3L);
    }

    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}" , articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }

    @Test
    void likePerformanceTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        likePerformanceTest(executorService, 157390014073294846L);
    }

    void likePerformanceTest(ExecutorService executorService, Long articleId) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(30000);

        log.info("count start");

        like(articleId, 1L);

        long start = System.nanoTime();

        for(int i = 0; i < 3000; i++) {
            long userId = i + 2;

            executorService.submit(() -> {
                like(articleId, userId);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        long end = System.nanoTime();

        log.info("time = " + (end - start) / 1000000 + "ms");
        log.info("count end");

        Long count = restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);

        log.info("count = " + count);
    }

    @Test
    void loginUserLikeTest() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNjk2ODE1MzQwMjIyMDk1MzYiLCJpYXQiOjE3NDQ1MjM0NzksImV4cCI6MTc0NDUyNzA3OX0.oI2xHHk5_J8DMWzshH5n1FiQOfuQx0AZ3_zJblG8it0";
        Long memberId = 169681534022209536L;
        Long articleId = 169690477028016128L;

        ResponseEntity<Void> response = gatewayRestClient.post()
                .uri("/v1/article-likes/articles/" + articleId)
                .header("Authorization", "Bearer " + token)
                .header("X-User-Id", String.valueOf(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        log.info("좋아요 성공 - status: {}", response.getStatusCode());
    }
}
