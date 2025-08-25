package livemarket.videocall.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
public class VideoCallNotificationDto {
    private final String sessionId;
    private final String fromMemberId;
    private final String toMemberId;
    private final String token;
    private final Long createdAt;
    private final String message;

    @JsonCreator
    public static VideoCallNotificationDto create(
            @JsonProperty("sessionId") String sessionId,
            @JsonProperty("fromMemberId") String fromMemberId,
            @JsonProperty("toMemberId") String toMemberId,
            @JsonProperty("token") String token,
            @JsonProperty("createdAt") Long createdAt,
            @JsonProperty("message") String message
    ) {
        return new VideoCallNotificationDto(sessionId, fromMemberId, toMemberId, token, createdAt, message);
    }

    private VideoCallNotificationDto(String sessionId, String fromMemberId, String toMemberId,
                                     String token, Long createdAt, String message) {
        this.sessionId = sessionId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.token = token;
        this.createdAt = createdAt;
        this.message = message;
    }
}
