package livemarket.member.repository;

import livemarket.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("select m.nickname from Member m where m.id = :memberId")
    String findNicknameByMemberId(@Param("memberId") Long memberId);
}
