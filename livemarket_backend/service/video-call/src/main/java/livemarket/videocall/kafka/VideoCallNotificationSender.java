package livemarket.videocall.kafka;

import livemarket.videocall.service.dto.VideoCallNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class VideoCallNotificationSender {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(VideoCallNotificationDto dto) {
        log.info("알림 전송 : {} -> {}", dto.getFromMemberId(), dto.getToMemberId());

        messagingTemplate.convertAndSend(
                "/topic/video-call/" + dto.getToMemberId(),
                dto
        );
    }
}
