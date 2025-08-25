package livemarket.videocall.controller;

import livemarket.videocall.service.VideoCallKafkaListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1/video-call-notify")
@RequiredArgsConstructor
public class VideoCallNotifyController {

    private final VideoCallKafkaListenerService kafkaListenerService;

    @GetMapping("/sse")
    public SseEmitter streamNotifications(@RequestParam("memberId") String memberId) {
        System.out.println("memberId = " + memberId);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        kafkaListenerService.registerEmitter(memberId, emitter);
        return emitter;
    }
}
