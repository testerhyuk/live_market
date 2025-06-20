package livemarket.videocall.repository;

import livemarket.videocall.entity.VideoCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoCallRepository extends JpaRepository<VideoCall, Long> {
    Optional<VideoCall> findByRoomId(String roomId);
}
