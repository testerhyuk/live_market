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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Log4j2
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    // memberId -> WebSocketSession 리스트
    private final Map<String, List<WebSocketSession>> memberSessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String memberId = (String) session.getAttributes().get("memberId");
        if (memberId != null) {
            memberSessions.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(session);
            log.info("Notification WebSocket 연결: memberId={}", memberId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        memberSessions.forEach((memberId, sessions) -> sessions.remove(session));
        log.info("Notification WebSocket 연결 종료: {}", session.getId());
    }

    public void sendNotification(String memberId, NotificationDto notificationDto) {
        List<WebSocketSession> sessions = memberSessions.get(memberId);
        if (sessions == null || sessions.isEmpty()) {
            log.warn("알림 전송 실패: 세션 없음 receiverId={}", memberId);
            return;
        }

        sessions.removeIf(session -> !session.isOpen()); // 닫힌 세션 제거

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(notificationDto)));
                    log.info("알림 전송 성공: {}", notificationDto);
                } catch (IOException e) {
                    log.error("알림 전송 실패", e);
                }
            }
        }
    }
}
