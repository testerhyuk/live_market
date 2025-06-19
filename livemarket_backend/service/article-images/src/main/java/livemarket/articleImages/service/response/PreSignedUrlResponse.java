package livemarket.articleImages.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreSignedUrlResponse {
    private String fileName;
    private String preSignedUrl;
}
