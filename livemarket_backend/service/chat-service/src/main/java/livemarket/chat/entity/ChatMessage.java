package livemarket.chat.entity;

import jakarta.persistence.*;
import livemarket.chat.dto.ChatMessageDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    private Long chatId;
    private String roomId;
    private String senderId;
    private String senderNickname;
    private String receiverId;
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime sentAt;
    @Enumerated(EnumType.STRING)
    private MessageType type;

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    public static ChatMessage from(ChatMessageDto dto, Long chatId) {
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.chatId = chatId;
        chatMessage.roomId = dto.getRoomId();
        chatMessage.senderId = dto.getSenderId();
        chatMessage.senderNickname = dto.getSenderNickname();
        chatMessage.receiverId = dto.getReceiverId();
        chatMessage.message = dto.getMessage();
        chatMessage.type = MessageType.valueOf(dto.getType().name());
        chatMessage.sentAt = LocalDateTime.now();

        return chatMessage;
    }
}
