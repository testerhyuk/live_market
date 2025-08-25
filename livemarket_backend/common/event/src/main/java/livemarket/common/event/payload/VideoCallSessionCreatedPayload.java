package livemarket.common.event.payload;

import livemarket.common.event.EventPayload;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoCallSessionCreatedPayload implements EventPayload {
    private String sessionId;
    private String token;
    private String requesterId;
    private String receiverId;
    private long createdAt;

    public static VideoCallSessionCreatedPayload of(String sessionId, String token, String requesterId, String receiverId, long createdAt) {
        VideoCallSessionCreatedPayload p = new VideoCallSessionCreatedPayload();

        p.sessionId = sessionId;
        p.token = token;
        p.requesterId = requesterId;
        p.receiverId = receiverId;
        p.createdAt = createdAt;

        return p;
    }
}
