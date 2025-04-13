package livemarket.member.service.response;

import livemarket.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignUpResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private String address;
    private String detailAddress;

    public static SignUpResponse from(Member member) {
        SignUpResponse response = new SignUpResponse();

        response.memberId = member.getMemberId();
        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.address = member.getAddress();
        response.detailAddress = member.getDetailAddress();

        return response;
    }
}
