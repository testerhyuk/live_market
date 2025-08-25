package livemarket.videocall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallEventWrapper {
    private Long eventId;
    private String type;
    private VideoCallNotificationDto payload;
}
