package livemarket.videocall.repository;

import livemarket.videocall.entity.VideoCallRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoCallRequestHistoryRepository extends JpaRepository<VideoCallRequestHistory, Long> {
    List<VideoCallRequestHistory> findByToMemberId(String toMemberId);
}
