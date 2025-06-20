package livemarket.videocall.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallKafkaResponseDto {
    private String roomId;
    private String responderId;
    private String status; // ACCEPTED, REJECTED
}
