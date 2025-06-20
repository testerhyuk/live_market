package livemarket.videocall.service;

import jakarta.transaction.Transactional;
import livemarket.common.event.EventType;
import livemarket.common.event.payload.VideoCallSessionCreatedPayload;
import livemarket.common.outboxmessagerelay.MessageRelayConstants;
import livemarket.common.outboxmessagerelay.OutboxEventPublisher;
import livemarket.common.snowflake.Snowflake;
import livemarket.videocall.entity.VideoCall;
import livemarket.videocall.entity.VideoCallRequestHistory;
import livemarket.videocall.kafka.VideoCallKafkaProducer;
import livemarket.videocall.kafka.VideoCallNotificationSender;
import livemarket.videocall.repository.VideoCallRepository;
import livemarket.videocall.repository.VideoCallRequestHistoryRepository;
import livemarket.videocall.service.dto.OpenViduSessionResult;
import livemarket.videocall.service.dto.VideoCallCreateDto;
import livemarket.videocall.service.dto.VideoCallNotificationDto;
import livemarket.videocall.service.dto.VideoCallSessionCreateDto;
import livemarket.videocall.service.dto.request.VideoCallKafkaRequestDto;
import livemarket.videocall.service.dto.response.VideoCallKafkaResponseDto;
import livemarket.videocall.service.dto.response.VideoCallSessionCreatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoCallService {
    private final VideoCallRepository videoCallRepository;
    private final VideoCallKafkaProducer kafkaProducer;
    private final OpenViduService openViduService;
    private final VideoCallRequestHistoryRepository historyRepository;
    private final VideoCallNotificationSender notificationSender;
    private final OutboxEventPublisher outboxEventPublisher;
    private final Snowflake snowflake = new Snowflake();

    public void handleRequest(VideoCallKafkaRequestDto dto) {
        VideoCallRequestHistory history = VideoCallRequestHistory.create(
                snowflake.nextId(),
                dto.getRoomId(),
                dto.getFromMemberId(),
                dto.getToMemberId(),
                dto.getMessage()
        );

        historyRepository.save(history);

        VideoCallNotificationDto notificationDto = new VideoCallNotificationDto(
                dto.getRoomId(),
                dto.getFromMemberId(),
                dto.getToMemberId(),
                dto.getMessage(),
                history.getRequestedAt()
        );

        notificationSender.sendNotification(notificationDto);
    }

    public void handleResponse(VideoCallKafkaResponseDto dto) {
        if ("ACCEPTED".equalsIgnoreCase(dto.getStatus())) {
            OpenViduSessionResult sessionResult = openViduService
                    .createSessionAndToken(dto.getRoomId(), dto.getResponderId());

            VideoCall session = VideoCall.create(
                    snowflake.nextId(),
                    dto.getRoomId(),
                    sessionResult.getSessionId(),
                    dto.getResponderId(),
                    sessionResult.getToken()
            );

            videoCallRepository.save(session);

            kafkaProducer.sendSessionCreated(new VideoCallSessionCreatedDto(
                    dto.getRoomId(),
                    sessionResult.getSessionId(),
                    sessionResult.getToken(),
                    dto.getResponderId()
            ));
        }
    }

    @Transactional
    public void createSessionAndPublish(VideoCallCreateDto dto) {
        Long videoCallId = snowflake.nextId();

        VideoCall videoCall = VideoCall.create(
                videoCallId,
                dto.getRoomId(),
                dto.getSessionId(),
                dto.getPublisherId(),
                dto.getToken()
        );

        videoCallRepository.save(videoCall);

        VideoCallSessionCreatedPayload payload = VideoCallSessionCreatedPayload.of(
                dto.getSessionId(),
                dto.getPublisherId(),
                dto.getReceiverId(),
                System.currentTimeMillis()
        );

        outboxEventPublisher.publish(
                EventType.VIDEO_CALL_SESSION_CREATED,
                payload,
                videoCallId % MessageRelayConstants.SHARD_COUNT
        );
    }
}
