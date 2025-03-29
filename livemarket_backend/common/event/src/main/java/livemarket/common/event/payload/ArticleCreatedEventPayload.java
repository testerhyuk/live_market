package livemarket.common.event.payload;

import livemarket.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreatedEventPayload implements EventPayload {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long boardArticleCount;
}
