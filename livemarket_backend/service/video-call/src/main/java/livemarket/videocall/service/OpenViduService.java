package livemarket.videocall.service;

import com.fasterxml.jackson.databind.JsonNode;
import livemarket.videocall.config.OpenViduProperties;
import livemarket.videocall.service.dto.OpenViduSessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class OpenViduService {
    private final OpenViduProperties properties;

    private WebClient getClient() {
        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeaders(headers -> {
                    String basicAuth = Base64.getEncoder()
                            .encodeToString(("OPENVIDUAPP:" + properties.getSecret()).getBytes());

                    headers.setBasicAuth(basicAuth);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    public OpenViduSessionResult createSessionAndToken(String roomId, String publisherId) {
        WebClient client = getClient();

        String sessionId = client.post()
                .uri("/openvidu/api/sessions")
                .bodyValue(Map.of("customSessionId", roomId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("id").asText())
                .block();

        log.info("세션 생성 완료 : {}", sessionId);

        String token = client.post()
                .uri("/openvidu/api/tokens")
                .bodyValue(Map.of("session", sessionId, "data", "publisherId=" + publisherId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("token").asText())
                .block();

        log.info("토큰 발급 완료 : {}", token);

        return new OpenViduSessionResult(sessionId, token);
    }
}
