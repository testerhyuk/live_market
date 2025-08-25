package livemarket.videocall.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VideoCallKafkaRequestDto {
    private String sessionId;
    private String fromMemberId;
    private String toMemberId;
    private String token;
    private Long createdAt;
    private String message;
}
