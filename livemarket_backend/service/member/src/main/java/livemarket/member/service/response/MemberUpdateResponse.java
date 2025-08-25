package livemarket.member.service.response;

import livemarket.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberUpdateResponse {
    private String email;
    private String nickname;
    private String address;
    private String detailAddress;

    public static MemberUpdateResponse of(Member member) {
        MemberUpdateResponse response = new MemberUpdateResponse();

        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.address = member.getAddress();
        response.detailAddress = member.getDetailAddress();

        return response;
    }
}
