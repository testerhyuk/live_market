package article.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PreSignedUrlListResponse {
    private List<PreSignedUrlResponse> urls;
}
