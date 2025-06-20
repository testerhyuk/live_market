package livemarket.videocall.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallResponseDto {
    private String roomId;
    private String status; // ACCEPTED, REJECTED
    private String responderId;
}
