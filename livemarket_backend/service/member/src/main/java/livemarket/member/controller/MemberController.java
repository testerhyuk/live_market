package livemarket.member.controller;

import livemarket.member.service.MemberService;
import livemarket.member.service.request.LoginRequest;
import livemarket.member.service.request.MemberUpdateRequest;
import livemarket.member.service.request.SignUpRequest;
import livemarket.member.service.response.LoginResponse;
import livemarket.member.service.response.MemberUpdateResponse;
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

    @GetMapping("/v1/members/nickname/{memberId}")
    public String getNickname(@PathVariable("memberId") String memberId) {
        return memberService.getNickname(Long.valueOf(memberId));
    }

    @GetMapping("/v1/members/info/{memberId}")
    public MemberUpdateResponse getMemberInfo(
            @PathVariable("memberId") String memberId) {
        Long member = Long.parseLong(memberId);

        return memberService.getMemberInfo(member);
    }

    @PutMapping("/v1/members/modify/{memberId}")
    public MemberUpdateResponse updateMember(
            @PathVariable("memberId") String memberId,
            @RequestBody MemberUpdateRequest request
    ) {
        Long member = Long.parseLong(memberId);

        return memberService.updateMember(member, request);
    }
}
