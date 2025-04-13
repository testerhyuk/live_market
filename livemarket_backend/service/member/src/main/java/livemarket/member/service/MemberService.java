package livemarket.member.service;

import livemarket.common.snowflake.Snowflake;
import livemarket.member.entity.Member;
import livemarket.member.repository.MemberRepository;
import livemarket.member.security.JwtProvider;
import livemarket.member.service.request.LoginRequest;
import livemarket.member.service.request.SignUpRequest;
import livemarket.member.service.response.LoginResponse;
import livemarket.member.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final Snowflake snowflake = new Snowflake();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public SignUpResponse signup(SignUpRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        Member member = Member.create(
                snowflake.nextId(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getAddress(),
                request.getDetailAddress(),
                request.getNickname()
        );

        return SignUpResponse.from(memberRepository.save(member));
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 틀렸습니다");
        }

        String token = jwtProvider.generateToken(String.valueOf(member.getMemberId()));

        return LoginResponse.from(member, token);
    }
}
