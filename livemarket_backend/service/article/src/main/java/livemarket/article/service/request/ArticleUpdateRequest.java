package livemarket.article.service.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleUpdateRequest {
    private String title;
    private String content;
    private List<String> newImageUrls;
    private List<String> deletedImageUrls;
}
