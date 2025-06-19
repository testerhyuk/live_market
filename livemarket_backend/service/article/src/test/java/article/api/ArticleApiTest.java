package article.api;

import livemarket.article.service.response.ArticlePageResponse;
import livemarket.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");
    RestClient gatewayRestClient = RestClient.create("http://localhost:9007");

//    @Test
//    void createTest() {
//        List<String> fileNames = List.of("image1.jpg", "image2.jpg");
//        List<String> presignedUrls = getPresignedUrls(fileNames);
//
//        ArticleResponse response = create(new ArticleCreateRequest(
//                "image Test3", "my Image content", 2L, 1L, presignedUrls
//        ));
//
//        log.info("response = " + response);
//    }
//
//    ArticleResponse create(ArticleCreateRequest request) {
//        return restClient.post()
//                .uri("/v1/articles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(request)
//                .retrieve()
//                .body(ArticleResponse.class);
//    }
//
//    @Test
//    void readTest() {
//        ArticleResponse response = read(163862518378237952L);
//        log.info("response = " + response);
//    }
//
//    ArticleResponse read(Long articleId) {
//        return restClient.get()
//                .uri("/v1/articles/{articleId}", articleId)
//                .retrieve()
//                .body(ArticleResponse.class);
//    }
//
//    @Test
//    void updateTest() {
//        Long articleId = 163859091183566848L;
//        ArticleResponse response = read(articleId);
//        log.info("response = " + response);
//
//        List<String> newImages = List.of("image3.jpg");
//        List<String> deletedImages = List.of("https://onairmarket.s3.ap-northeast-2.amazonaws.com/image1.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250328T035731Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIASU5664XZSW3TKAJ6%2F20250328%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=180&X-Amz-Signature=7a58b9dc35060d11eaa67b437f8bbe434f0170cdd884bfda184c4a76bae17b33");
//
//        List<String> presignedUrls = getPresignedUrls(newImages);
//
//        List<String> updatedImages = new ArrayList<>(response.getImageUrls());
//        updatedImages.removeAll(deletedImages);
//        updatedImages.addAll(presignedUrls);
//
//        ArticleUpdateRequest updateRequest = new ArticleUpdateRequest(
//                "updated title",
//                "updated content",
//                presignedUrls,
//                deletedImages
//        );
//
//        update(articleId, updateRequest);
//
//        ArticleResponse articleResponse = read(articleId);
//        log.info("updated article : " + articleResponse);
//    }
//
//    void update(Long articleId, ArticleUpdateRequest updateRequest) {
//        restClient.put()
//                .uri("/v1/articles/{articleId}", articleId)
//                .body(updateRequest)
//                .retrieve()
//                .body(ArticleResponse.class);
//    }
//
//    @Test
//    void deleteTest() {
//        restClient.delete()
//                .uri("/v1/articles/{articleId}", 163862518378237952L)
//                .retrieve()
//                .body(ArticleResponse.class);
//    }
//
//    @Test
//    void readAllTest() {
//        ArticlePageResponse response = restClient.get()
//                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
//                .retrieve()
//                .body(ArticlePageResponse.class);
//
//        log.info("response.getArticleCount() = " + response.getArticleCount());
//
//        for (ArticleResponse article : response.getArticles()) {
//            log.info("articleId = " + article.getArticleId());
//        }
//    }
//
//    @Test
//    void readAllInfiniteScrollTest() {
//        List<ArticleResponse> articles1 = restClient.get()
//                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
//                .retrieve()
//                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
//                });
//
//        log.info("firstPage");
//
//        for (ArticleResponse articleResponse : articles1) {
//            log.info("articleResponse.getArticleId() = " + articleResponse.getArticleId());
//        }
//
//        Long lastArticleId = articles1.getLast().getArticleId();
//
//        List<ArticleResponse> articles2 = restClient.get()
//                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
//                .retrieve()
//                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
//                });
//
//        log.info("secondPage");
//
//        for (ArticleResponse articleResponse : articles2) {
//            log.info("articleResponse.getArticleId() = " + articleResponse.getArticleId());
//        }
//    }
//
//    @Test
//    void countTest() {
//        ArticleResponse response = create(new ArticleCreateRequest("hi", "content", 3L, 12L, List.of("image1.jpg")));
//
//        Long count1 = restClient.get()
//                .uri("/v1/articles/boards/{boardId}/count", 12L)
//                .retrieve()
//                .body(Long.class);
//
//        log.info("count1 = " + count1);
//
//        restClient.delete()
//                .uri("/v1/articles/{articleId}", response.getArticleId())
//                .retrieve()
//                .body(ArticleResponse.class);
//
//        Long count2 = restClient.get()
//                .uri("/v1/articles/boards/{boardId}/count", 12L)
//                .retrieve()
//                .body(Long.class);
//
//        log.info("count2 = " + count2);
//    }
//
//    @Test
//    void ConcurrencyCountTest() throws InterruptedException {
//        Long boardId = 12L;
//        int threadCount = 10;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final Long writerId = (long) (300 + i);
//            executorService.execute(() -> {
//                try {
//                    create(new ArticleCreateRequest("동시성 테스트", "내용", writerId, boardId, List.of("image1.jpg")));
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        // 결과 검증
//        Long articleCount = restClient.get()
//                .uri("/v1/articles/boards/{boardId}/count", boardId)
//                .retrieve()
//                .body(Long.class);
//
//        log.info("최종 articleCount = " + articleCount);
//        assertThat(articleCount).isEqualTo(threadCount);
//    }
//
//    List<String> getPresignedUrls(List<String> fileNames) {
//        PreSignedUrlListResponse response = restClient.post()
//                .uri("/presigned-urls")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(fileNames)
//                .retrieve()
//                .body(PreSignedUrlListResponse.class);
//
//        List<String> urls = response.getUrls().stream()
//                .map(PreSignedUrlResponse::getPreSignedUrl)
//                .toList();
//
//        log.info("Presigned URLs : " + urls);
//
//        return urls;
//    }
//
//    @Test
//    void loginAndCreateArticleTest() {
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNjk2ODE1MzQwMjIyMDk1MzYiLCJpYXQiOjE3NDQ1MjM0NzksImV4cCI6MTc0NDUyNzA3OX0.oI2xHHk5_J8DMWzshH5n1FiQOfuQx0AZ3_zJblG8it0";
//        Long memberId = 169681534022209536L;
//
//        ArticleCreateRequest request = new ArticleCreateRequest(
//                "로그인 유저 게시글 작성 테스트",
//                "테스트",
//                memberId,
//                1L,
//                List.of("test.jpg")
//        );
//
//        ArticleResponse response = gatewayRestClient.post()
//                .uri("/v1/articles")
//                .header("Authorization", "Bearer " + token)
//                .header("X-User-Id", String.valueOf(memberId))
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(request)
//                .retrieve()
//                .body(ArticleResponse.class);
//
//        log.info("response = " + response);
//    }
//
//    @Getter
//    @AllArgsConstructor
//    static class ArticleCreateRequest {
//        private String title;
//        private String content;
//        private Long writerId;
//        private Long boardId;
//        private List<String> imageUrls;
//    }
//
//    @Getter
//    @AllArgsConstructor
//    static class ArticleUpdateRequest {
//        private String title;
//        private String content;
//        private List<String> newImageUrls;
//        private List<String> deletedImageUrls;
//    }
}
