package livemarket.videocall.service;

import com.fasterxml.jackson.databind.JsonNode;
import livemarket.videocall.config.OpenViduProperties;
import livemarket.videocall.service.dto.OpenViduSessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
                    headers.setBasicAuth("OPENVIDUAPP", properties.getSecret());
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    public OpenViduSessionResult createSessionAndToken(String roomId, String publisherId) {
        WebClient client = getClient();
        String sessionId;

        try {
            sessionId = client.post()
                    .uri("/openvidu/api/sessions")
                    .bodyValue(Map.of("customSessionId", roomId))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.get("id").asText())
                    .block();
        } catch (WebClientResponseException.Conflict e) {
            // 이미 존재하면 기존 세션 사용
            sessionId = roomId;
        }

        String token = client.post()
                .uri("/openvidu/api/tokens")
                .bodyValue(Map.of("session", sessionId, "data", "publisherId=" + publisherId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("token").asText())
                .block();

        return new OpenViduSessionResult(sessionId, token);
    }

    public OpenViduSessionResult joinSessionAndGetToken(String sessionId, String participantId) {
        WebClient client = getClient();

        try {
            client.post()
                    .uri("/openvidu/api/sessions")
                    .bodyValue(Map.of("customSessionId", sessionId))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (WebClientResponseException.Conflict e) {}

        String token = client.post()
                .uri("/openvidu/api/tokens")
                .bodyValue(Map.of(
                        "session", sessionId,
                        "data", "participantId=" + participantId
                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("token").asText())
                .block();

        return new OpenViduSessionResult(sessionId, token);
    }
}
