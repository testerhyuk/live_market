package livemarket.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    private Long memberId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false, name = "detail_address")
    private String detailAddress;
    @Column(nullable = false)
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Member create(Long memberId, String email, String password,
                                String address, String detailAddress, String nickname
                                ) {
        Member member = new Member();

        member.memberId = memberId;
        member.email = email;
        member.password = password;
        member.address = address;
        member.detailAddress = detailAddress;
        member.nickname = nickname;
        member.createdAt = LocalDateTime.now();
        member.updatedAt = LocalDateTime.now();

        return member;
    }

    public void update(String password, String nickname, String address, String detailAddress) {
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;
        if (address != null) this.address = address;
        if (detailAddress != null) this.detailAddress = detailAddress;
        this.updatedAt = LocalDateTime.now();
    }
}
