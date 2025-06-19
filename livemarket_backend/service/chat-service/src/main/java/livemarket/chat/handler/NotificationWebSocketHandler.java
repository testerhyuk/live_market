package livemarket.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.chat.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String memberId = (String) session.getAttributes().get("memberId");
        log.info("Notification WebSocket 연결됨 memberId={}", memberId);

        if (memberId == null) {
            log.warn("memberId is null in afterConnectionEstablished!");
        } else {
            userSessions.put(memberId, session);
            log.info("Notification 세션 저장 완료 memberId={}", memberId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userSessions.values().remove(session);
    }

    public void sendNotification(NotificationDto dto) throws IOException {
        log.info("sendNotification 호출: receiverId={}", dto.getReceiverId());
        WebSocketSession session = userSessions.get(dto.getReceiverId());
        if (session != null && session.isOpen()) {
            String json = objectMapper.writeValueAsString(dto);
            session.sendMessage(new TextMessage(json));
            log.info("알림 전송 성공: {}", json);
        } else {
            log.warn("알림 전송 실패: 세션 없거나 닫힘 receiverId={}", dto.getReceiverId());
        }
    }
}
