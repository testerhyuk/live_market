package livemarket.videocall.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoCallCreateDto {
    private String roomId;
    private String sessionId;
    private String publisherId;
    private String receiverId;
    private String token;
}
