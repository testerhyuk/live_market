package livemarket.chat.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.chat.dto.NotificationDto;
import livemarket.chat.handler.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationRedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final NotificationWebSocketHandler handler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            NotificationDto dto = objectMapper.readValue(json, NotificationDto.class);
            handler.sendNotification(dto);
        } catch (Exception e) {
            log.error("알림 수신 에러", e);
        }
    }
}