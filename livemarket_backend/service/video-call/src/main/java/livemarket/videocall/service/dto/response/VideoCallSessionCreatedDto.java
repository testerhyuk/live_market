package livemarket.videocall.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallSessionCreatedDto {
    private String roomId;
    private String sessionId;
    private String token;
    private String createdBy;
}
