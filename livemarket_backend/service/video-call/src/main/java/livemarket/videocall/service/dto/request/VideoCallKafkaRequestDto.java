package livemarket.videocall.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallKafkaRequestDto {
    private String roomId;
    private String fromMemberId;
    private String toMemberId;
    private String message;
}
