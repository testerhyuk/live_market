package livemarket.videocall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoCallRequestHistory {
    @Id
    private Long videoCallHistoryId;
    private String roomId;
    private String fromMemberId;
    private String toMemberId;
    private String message;
    private LocalDateTime requestedAt;

    public static VideoCallRequestHistory create(Long videoCallHistoryId, String roomId, String fromMemberId, String toMemberId, String message) {
        VideoCallRequestHistory history = new VideoCallRequestHistory();

        history.videoCallHistoryId = videoCallHistoryId;
        history.roomId = roomId;
        history.fromMemberId = fromMemberId;
        history.toMemberId = toMemberId;
        history.message = message;
        history.requestedAt = LocalDateTime.now();

        return history;
    }
}
