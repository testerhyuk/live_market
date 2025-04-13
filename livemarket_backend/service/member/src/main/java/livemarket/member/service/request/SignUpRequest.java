package livemarket.member.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignUpRequest {
    private String email;
    private String password;
    private String passwordConfirm;
    private String address;
    private String detailAddress;
    private String nickname;
}
