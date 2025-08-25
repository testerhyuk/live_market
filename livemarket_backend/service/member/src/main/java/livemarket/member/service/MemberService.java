package livemarket.member.service;

import livemarket.common.snowflake.Snowflake;
import livemarket.jwt.JwtProvider;
import livemarket.member.entity.Member;
import livemarket.member.repository.MemberRepository;
import livemarket.member.service.request.LoginRequest;
import livemarket.member.service.request.MemberUpdateRequest;
import livemarket.member.service.request.SignUpRequest;
import livemarket.member.service.response.LoginResponse;
import livemarket.member.service.response.MemberUpdateResponse;
import livemarket.member.service.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public String getNickname(Long memberId) {
        return memberRepository.findNicknameByMemberId(memberId);
    }

    @Transactional
    public MemberUpdateResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        String rawPassword = request.getPassword();
        String encodedPassword = null;

        if (rawPassword != null && !rawPassword.isBlank()) {
            encodedPassword = passwordEncoder.encode(rawPassword);
        }

        member.update(
                encodedPassword,
                request.getNickname(),
                request.getAddress(),
                request.getDetailAddress()
        );

        return MemberUpdateResponse.of(member);
    }

    @Transactional
    public MemberUpdateResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        return MemberUpdateResponse.of(member);
    }
}
