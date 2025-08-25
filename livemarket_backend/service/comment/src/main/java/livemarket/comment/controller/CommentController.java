package livemarket.comment.controller;

import livemarket.comment.service.request.CommentUpdateRequest;
import livemarket.comment.service.response.CommentPageResponse;
import livemarket.comment.service.response.CommentResponse;
import livemarket.comment.service.CommentService;
import livemarket.comment.service.request.CommentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/v1/comments/{commentId}")
    public CommentResponse read(
            @PathVariable("commentId") Long commentId
    ) {
        return commentService.read(commentId);
    }

    @PostMapping("/v1/comments")
    public CommentResponse create(@RequestBody CommentCreateRequest request,
                                  @RequestHeader("X-User-Id") String memberId) {
        return commentService.create(request, memberId);
    }

    @PutMapping("/v1/comments/{commentId}")
    public CommentResponse update(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentUpdateRequest request,
            @RequestHeader("X-User-Id") String userId
    ) throws AccessDeniedException {
        return commentService.update(commentId, request, Long.valueOf(userId));
    }

    @DeleteMapping("/v1/comments/{commentId}")
    public void delete(
            @PathVariable("commentId") String commentId,
            @RequestHeader("X-User-Id") String userId
        ) throws AccessDeniedException {
        commentService.delete(Long.valueOf(commentId), Long.valueOf(userId));
    }

    @GetMapping("/v1/comments")
    public CommentPageResponse readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize
    ) {
        return commentService.readAll(articleId, page, pageSize);
    }

    // 무한 스크롤
    @GetMapping("/v1/comments/infinite-scroll")
    public List<CommentResponse> readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @RequestParam("pageSize") Long pageSize
    ) {
        return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
    }

    @GetMapping("/v1/comments/articles/{articleId}/count")
    public Long count(
            @PathVariable("articleId") Long articleId
    ) {
        return commentService.count(articleId);
    }

    @GetMapping("/v1/comments/member/{userId}")
    public List<CommentResponse> getCommentByUserId(@PathVariable("userId") String userId) {
        Long memberId = Long.parseLong(userId);

        return commentService.getCommentByUserId(memberId);
    }
}
