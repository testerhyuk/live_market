package livemarket.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String receiverId;
    private String senderId;
    private String roomId;
    private String content;
    private String type;
}
