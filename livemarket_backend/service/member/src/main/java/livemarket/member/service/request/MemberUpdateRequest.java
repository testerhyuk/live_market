package livemarket.member.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MemberUpdateRequest {
    private String password;
    private String address;
    private String detailAddress;
    private String nickname;

    public MemberUpdateRequest(String password, String address, String detailAddress, String nickname) {
        this.password = password;
        this.address = address;
        this.detailAddress = detailAddress;
        this.nickname = nickname;
    }
}
