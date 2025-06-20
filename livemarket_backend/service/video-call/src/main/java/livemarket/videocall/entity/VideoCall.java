package livemarket.videocall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoCall {
    @Id
    private Long videoCallId;
    private String roomId;
    private String sessionId;
    private String publisherId;
    private String token;
    private LocalDateTime createdAt;

    public static VideoCall create(Long videoCallId, String roomId, String sessionId, String publisherId, String token) {
        VideoCall videoCall = new VideoCall();

        videoCall.videoCallId = videoCallId;
        videoCall.roomId = roomId;
        videoCall.sessionId = sessionId;
        videoCall.publisherId = publisherId;
        videoCall.token = token;
        videoCall.createdAt = LocalDateTime.now();

        return videoCall;
    }
}
