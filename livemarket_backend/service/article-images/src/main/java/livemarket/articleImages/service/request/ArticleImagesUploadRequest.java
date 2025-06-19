package livemarket.articleImages.service.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleImagesUploadRequest {
    private Long articleId;
    private Long userId;
    private List<String> imageUrls;
}
