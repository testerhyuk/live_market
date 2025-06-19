package livemarket.chat.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import livemarket.chat.dto.ChatMessageDto;
import livemarket.chat.dto.NotificationDto;
import livemarket.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RedisPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, ChatMessageDto message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topic, json);
            log.info("Redis 발행 완료 topic={}, message={}", topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis publish 실패", e);
        }
    }

    public void publishNotification(NotificationDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.convertAndSend("notification", json);
        } catch (Exception e) {
            throw new RuntimeException("Redis publish 실패", e);
        }
    }
}
