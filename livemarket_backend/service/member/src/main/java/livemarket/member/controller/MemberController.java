package livemarket.member.controller;

import livemarket.member.service.MemberService;
import livemarket.member.service.request.LoginRequest;
import livemarket.member.service.request.SignUpRequest;
import livemarket.member.service.response.LoginResponse;
import livemarket.member.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/v1/members/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest request) {
        return memberService.signup(request);
    }

    @PostMapping("/v1/members/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, response.getTokenType() + " " + response.getToken())
                .body(response);
    }
}
