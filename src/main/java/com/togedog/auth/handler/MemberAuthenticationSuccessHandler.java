package com.togedog.auth.handler;

import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository; // 추가

    public MemberAuthenticationSuccessHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
        // 인증 성공 후, 로그를 기록하거나 사용자 정보를 response로 전송하는 등의 추가 작업을 할 수 있다.
        // Member의 status를 변경해주는 로직
        Member member = memberRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        member.setStatus(Member.memberStatus.LOGGED_IN);
        memberRepository.save(member);

        log.info("# Authenticated successfully!");
    }
}
