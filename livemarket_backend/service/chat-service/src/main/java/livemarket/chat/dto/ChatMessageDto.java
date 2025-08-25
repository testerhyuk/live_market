package livemarket.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageDto {
    private String roomId;
    private String senderId;
    private String receiverId;
    private String message;
    private String senderNickname;
    private LocalDateTime sentAt;
    private MessageType type;
    private String source;

    public enum MessageType {
        ENTER, TALK, QUIT
    }
}
