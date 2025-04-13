package livemarket.member.service.response;

import livemarket.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginResponse {
    private Long memberId;
    private String email;
    private String token;
    private String tokenType;

    public static LoginResponse from(Member member, String token) {
        LoginResponse response = new LoginResponse();

        response.memberId = member.getMemberId();
        response.email = member.getEmail();
        response.token = token;
        response.tokenType = "Bearer";

        return response;
    }
}
