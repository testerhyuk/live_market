package livemarket.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketGatewayConfig {
    @Bean
    public RouteLocator webSocketRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Chat WebSocket
                .route("chat-ws", r -> r.path("/ws/chat/**")
                        .uri("ws://localhost:9020"))
                .route("video-call-ws", r -> r.path("/ws/video-calls/**")
                        .filters(f -> f
                                .removeRequestHeader("Cookie")
                                .setResponseHeader("Sec-WebSocket-Protocol", "v10.stomp,v11.stomp,v12.stomp")
                        )
                        .uri("ws://localhost:9021")
                )
                .build();
    }
}
