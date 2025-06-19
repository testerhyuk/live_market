package livemarket.chat.interceptor;

import livemarket.chat.config.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.newInstance()
                .query(query)
                .build()
                .getQueryParams();

        String token = queryParams.getFirst("token");
        String roomId = queryParams.getFirst("roomId");

        log.info("roomId = " + roomId);

        // 검증 및 세션 속성에 저장
        if (token == null || roomId == null) {
            return false;
        }

        String memberId = jwtProvider.getMemberIdFromToken(token);
        if (memberId == null) {
            return false;
        }

        attributes.put("memberId", memberId.toString());
        attributes.put("roomId", roomId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
