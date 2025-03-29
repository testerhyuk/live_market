package livemarket.article.repository;

import livemarket.article.entity.BoardArticleCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BoardArticleCount> findLockedByBoardId(Long boardId);
}
