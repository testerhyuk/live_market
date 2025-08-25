package livemarket.articleImages.service.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleImagesUploadRequest {
    private String articleId;
    private String userId;
    private List<String> imageUrls;
}
