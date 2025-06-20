package livemarket.videocall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallSessionCreateDto {
    private String sessionId;
    private String requesterId;
    private String receiverId;
}
