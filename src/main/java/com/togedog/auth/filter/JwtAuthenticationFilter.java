package com.togedog.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.togedog.auth.dto.LoginDto;
import com.togedog.auth.jwt.JwtTokenizer;
import com.togedog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;

    /**
    * 사용자가 로그인 할 때 실행되는 메서드, 사용자의 email과 비밀번호를 읽어와서 token을 만든 후 사용자가 맞는지 판단.
    *
    * @param source JSON 형식의 문자열
    * @return 사용자가 일치 여부에 따른 메서드 실행 후 반환되는 값
    * @author Tizesin(신민준)
    */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    /**
    * attemptAuthentication에서 사용자가 맞다고 판단되면 실행되는 메서드
    *
    * @param source JSON 형식의 문자열
    * @return 반환값 내용
    * @throws 예외처리
    * @author Tizesin(신민준)
    */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {
        Member member = (Member) authResult.getPrincipal();

        String accessToken = delegateAccessToken(member);
        String refreshToken = delegateRefreshToken(member, accessToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);  // 추가
    }

    /**
    * AccessToken 생성하는 메서드
    *
    * @param member의 정보
    * @return AccessToken을 만들어서 반환
    * @author Tizesin(신민준), 황진혁
    */
    private String delegateAccessToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
//        claims.put("memberId", member.getMemberId());
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    /**
     * RefreshToken 생성하는 메서드
     *
     * @param member의 정보
     * @return RefreshToken을 만들어서 반환
     * @author Tizesin(신민준), 황진혁
     */
    private String delegateRefreshToken(Member member, String accessToken) {
        // 수정
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());

        // 수정
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey, accessToken);

        return refreshToken;
    }

//    private URI createURI(String accessToken, String refreshToken) {
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("access_token", accessToken);
//        queryParams.add("refresh_token", refreshToken);
//
//        return UriComponentsBuilder
//                .newInstance()
//                .scheme("http")
//                .host("localhost")
////                .port(80)
//                .path("/receive-token.html")
//                .queryParams(queryParams)
//                .build()
//                .toUri();
//    }
}