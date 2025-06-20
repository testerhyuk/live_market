package livemarket.videocall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallNotificationDto {
    private String roomId;
    private String fromMemberId;
    private String toMemberId;
    private String message;
    private LocalDateTime requestedAt;
}
