package livemarket.member.api;

import livemarket.member.service.response.LoginResponse;
import livemarket.member.service.response.SignUpResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;


@Log4j2
public class MemberApiTest {
    RestClient memberRestClient = RestClient.create("http://localhost:9006");

    @Test
    void signUpTest() {
        SignUpResponse response = signUp(new SignUpRequest(
                "test2@gmail.com", "password123", "password123",
                "서울시 강남구", "123-456", "테스트2"
        ));

        log.info("response = " + response);
    }

    SignUpResponse signUp(SignUpRequest request) {
        return memberRestClient.post()
                .uri("/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SignUpResponse.class);
    }

    @Test
    void loginTest() {
        LoginResponse response = login(new LoginRequest(
                "test@gmail.com",
                "password123"
        ));

        log.info("login response = " + response);
        log.info("login token = " + response.getToken());
    }

    private LoginResponse login(LoginRequest request) {
        return memberRestClient.post()
                .uri("/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LoginResponse.class);
    }

    @Getter
    @AllArgsConstructor
    static class SignUpRequest {
        private String email;
        private String password;
        private String passwordConfirm;
        private String address;
        private String detailAddress;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    static class LoginRequest {
        private String email;
        private String password;
    }
}
