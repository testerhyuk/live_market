package comment.api;

import livemarket.comment.service.response.CommentPageResponse;
import livemarket.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");
    RestClient gatewayRestClient = RestClient.create("http://localhost:9007");

    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 159254097421717504L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 159254098193469440L)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse comment : responses1) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        Long lastParentCommentId = responses1.getLast().getParentCommentId();
        Long lastCommentId = responses1.getLast().getCommentId();

        List<CommentResponse> responses2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse comment : responses2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void countTest() {
        CommentResponse commentResponse = createComment(new CommentCreateRequest(6L, "my comment1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v1/comments/articles/{articleId}/count", 6L)
                .retrieve()
                .body(Long.class);

        System.out.println("count1 = " + count1);

        restClient.delete()
                .uri("/v1/comments/{commentId}", commentResponse.getCommentId())
                .retrieve()
                .body(CommentResponse.class);

        Long count2 = restClient.get()
                .uri("/v1/comments/articles/{articleId}/count", 6L)
                .retrieve()
                .body(Long.class);

        System.out.println("count2 = " + count2);
    }

    @Test
    void loginAndCreateCommentTest() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNjk2ODE1MzQwMjIyMDk1MzYiLCJpYXQiOjE3NDQ1MjM0NzksImV4cCI6MTc0NDUyNzA3OX0.oI2xHHk5_J8DMWzshH5n1FiQOfuQx0AZ3_zJblG8it0";
        Long memberId = 169681534022209536L;

        CommentCreateRequest request = new CommentCreateRequest(
                169690477028016128L,
                "로그인 유저 댓글 테스트",
                null,
                169681534022209536L
        );

        CommentResponse response = gatewayRestClient.post()
                .uri("/v1/comments")
                .header("Authorization", "Bearer " + token)
                .header("X-User-Id", String.valueOf(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);

        log.info("response = " + response);
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }
}
