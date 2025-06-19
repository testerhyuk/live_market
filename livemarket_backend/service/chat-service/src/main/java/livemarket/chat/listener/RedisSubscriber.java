package livemarket.chat.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import livemarket.chat.dto.ChatMessageDto;
import livemarket.chat.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            ChatMessageDto chatMessageDto = objectMapper.readValue(json, ChatMessageDto.class);

            String roomId = chatMessageDto.getRoomId();
            chatWebSocketHandler.sendMessageToRoom(roomId, chatMessageDto);

            log.info("RedisSubscriber 메시지 처리 완료 roomId={}, sender={}", roomId, chatMessageDto.getSenderId());
        } catch (Exception e) {
            log.error("Redis Subscriber 오류 : " + e.getMessage(), e);
        }
    }
}
