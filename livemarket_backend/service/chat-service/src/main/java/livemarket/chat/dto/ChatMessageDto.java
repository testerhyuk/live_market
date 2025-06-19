package livemarket.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String roomId;
    private String senderId;
    private String receiverId;
    private String message;
    private LocalDateTime sentAt;
    private MessageType type;

    public enum MessageType {
        ENTER, TALK, QUIT
    }
}
