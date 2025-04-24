package com.togedog.auth.filter;

import com.togedog.auth.jwt.JwtTokenizer;
import com.togedog.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final RedisTemplate<String, Object> redisTemplate; // 추가

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer,
                                 CustomAuthorityUtils authorityUtils, RedisTemplate redisTemplate) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.redisTemplate = redisTemplate;
    }

//    /**
//     * 요청이 들어올 때마다 실행 되는 메서드
//     * verifyJws 메서드를 통해 요청에 있는 token값을 확인
//     *
//     * @param source JSON 형식의 문자열
//     * @return 반환값 내용
//     * @throws 예외처리
//     * @author Tizesin(신민준)
//     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);
            isTokenValidInRedis(claims); // 추가
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (NullPointerException ne) {
            request.setAttribute("exception", ne);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
//
//    /**
//    * 요청에서 JWT토큰을 꺼내여 반환하는 메서드
//    *
//    * @param source JSON 형식의 문자열
//    * @return 반환값 내용
//    * @throws 예외처리
//    * @author Tizesin(신민준)
//    */
    public Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }

//    /**
//    * 특정 조건에서 필터를 사용하지 않을 때 사용되는 메서드
//    * Authorization 헤더가 없거나 Bearer로 시작하지 않으면 필터를 적용하지 않음
//     *
//    * @param source JSON 형식의 문자열
//    * @return 반환값 내용
//    * @throws 예외처리
//    * @author Tizesin(신민준)
//    */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    /**
//    * claims에서 memberId 값을 추출하는 메서드
//    *
//    * @param JWT에서의 claims 정보
//    * @return JWT에서의 memberId 값
//    * @author Tizesin(신민준)
//    */
    public static String getMemberIdFromClaims(Map<String, Object> claims) {
        return (String) claims.get("memberId");
    }

    // Redis에서 토큰을 검증하는 메서드 추가
    private boolean isTokenValidInRedis(Map<String, Object> claims) {
        String username = (String) claims.get("username");

        Boolean hasKey = Optional.ofNullable(redisTemplate.hasKey(username))
                .orElseThrow(() -> new NullPointerException("Redis key check returned null"));

        return hasKey;
    }
}