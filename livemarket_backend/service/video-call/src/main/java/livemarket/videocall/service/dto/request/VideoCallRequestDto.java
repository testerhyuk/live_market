package livemarket.videocall.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallRequestDto {
    private String roomId;
    private String fromMemberId;
    private String toMemberId;
}
