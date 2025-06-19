package livemarket.articleImages.service.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleImagesUpdateRequest {
    private Long articleId;
    private Long userId;
    private List<String> newImageUrls;
    private List<String> remainingImageIds;
}
