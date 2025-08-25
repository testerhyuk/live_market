package livemarket.chat.controller;

import livemarket.chat.entity.ChatMessage;
import livemarket.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/v1/chat/rooms/{roomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable("roomId") String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }
}
