package livemarket.chat.config;

import livemarket.chat.handler.ChatWebSocketHandler;
import livemarket.chat.handler.NotificationWebSocketHandler;
import livemarket.chat.interceptor.AuthHandshakeInterceptor;
import livemarket.chat.interceptor.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler handler;
    private final JwtHandshakeInterceptor interceptor;
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/chat")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*");

        registry.addHandler(notificationWebSocketHandler, "/ws/notification")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
