package livemarket.chat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.chat.dto.ChatMessageDto;
import livemarket.chat.entity.ChatMessage;
import livemarket.chat.publisher.RedisPublisher;
import livemarket.chat.repository.ChatMessageRepository;
import livemarket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Log4j2
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final RedisPublisher redisPublisher;
    private final ChatMessageRepository chatMessageRepository;
    private final Snowflake snowflake = new Snowflake();

    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String memberId = (String) session.getAttributes().get("memberId");
        String roomId = (String) session.getAttributes().get("roomId");

        sessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        log.info("WebSocket 연결 성공 memberId: {}, roomId= {}", memberId, roomId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessageDto dto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);

        Long chatId = snowflake.nextId();
        String roomId = dto.getRoomId();

        ChatMessage chatMessage = ChatMessage.from(dto, chatId);
        chatMessageRepository.save(chatMessage);

        redisPublisher.publish("chatroom:" + roomId, dto);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().forEach(set -> set.remove(session));
        log.info("WebSocket 연결 종료");
    }

    public void sendMessageToRoom(String roomId, ChatMessageDto dto) throws JsonProcessingException {
        Set<WebSocketSession> sessionSet = sessions.get(roomId);

        if (sessionSet == null) return;

        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(dto));
        for (WebSocketSession session : sessionSet) {
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                log.error("WebSocket send 실패", e);
            }
        }
    }
}
