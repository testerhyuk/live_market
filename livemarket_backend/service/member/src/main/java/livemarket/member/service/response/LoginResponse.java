package livemarket.member.service.response;

import livemarket.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginResponse {
    private String memberId;
    private String email;
    private String nickname;
    private String token;
    private String tokenType;

    public static LoginResponse from(Member member, String token) {
        LoginResponse response = new LoginResponse();

        response.memberId = String.valueOf(member.getMemberId());
        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.token = token;
        response.tokenType = "Bearer";

        return response;
    }
}
