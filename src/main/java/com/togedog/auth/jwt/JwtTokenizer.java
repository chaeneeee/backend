package com.togedog.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenizer {
    // RedisTemplate을 사용하여 Redis 서버와의 상호작용을 처리하는 필드를 선언.
    private final RedisTemplate<String, Object> redisTemplate;
    // 의존성 주입
    public JwtTokenizer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        // Redis의 ListOperations 객체를 사용하여 리스트 형태로 데이터를 처리.
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // claims에 저장된 username(이메일)을 키로 accessToken 값을 추가.
        valueOperations.set((String) claims.get("username"), accessToken, accessTokenExpirationMinutes, TimeUnit.MINUTES);
        return accessToken;
    }

    // redis에 accessToken을 키로 사용하기 위해, accessToken을 함께 전달받습니다.
    public String generateRefreshToken(String subject,
                                       Date expiration,
                                       String base64EncodedSecretKey,
                                       String accessToken) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        String refreshToken = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        // Redis의 ListOperations 객체를 사용하여 리스트 형태로 데이터를 처리.
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // "accessToken"이라는 키에 accessToken 값을 리스트에 추가.
        valueOperations.set(accessToken, refreshToken, refreshTokenExpirationMinutes, TimeUnit.MINUTES);

        return refreshToken;
    }

    // 검증 후, Claims을 반환 하는 용도
    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
        return claims;
    }

    // 단순히 검증만 하는 용도로 쓰일 경우
    public void verifySignature(String jws, String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    /**
     * websocket에서 사용자 토큰 인증 정보 인터셉터 후 토큰 내 정보 뽑는 메서드
     *
     * @param 토큰 문자열 값
     * @return 반환값 내용
     * @author Tizesin(신민준)
     */
//    //토큰에서 회원정보 추출
//    public String getId(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//    //JWT 토큰에서 인증 정보 조회
//    public Authentication getAuthentication(String token) {
//        Member member = MemberRepository.findById(Long.valueOf(this.getId(token)));
//        return new JwtAuthenticationToken(member, null);
//    }

    /**
     * websocket을 통한 통신을 하기전 진행하는 handshake에서 사용자 인증 정보를 확인 하기 위함
     *
     * @param accessToken과 암호화할 secretkey값
     * @return 토큰안의 claims 정보
     * @throws ExpiredJwtException / MalformedJwtException
     * @author Tizesin(신민준)
     */
    public static Claims getClaimsFromAccessToken(String accessToken, String base64EncodedSecretKey)
            throws ExpiredJwtException, MalformedJwtException {

        Key key = getKeyFromBase64EncodedKeyForWebSocket(base64EncodedSecretKey);

        try {
            // 토큰을 파싱하여 Claims를 추출
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 검증을 위한 키 설정
                    .build()
                    .parseClaimsJws(accessToken) // 토큰을 파싱하여 클레임 반환
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            // 잘못된 토큰 형식일 경우
            throw new MalformedJwtException("잘못된 토큰 형식입니다.");
        }
    }

    /**
     * getTokenExpiration의 설명을 여기에 작성한다.
     *
     * @param 발행일 기준 만료일 설정 시간
     * @return 만료일 계산 결과
     * @author Tizesin(신민준)
     */
    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    private static Key getKeyFromBase64EncodedKeyForWebSocket(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    // 로그아웃시 레디스에서 email를 기준으로 토큰 값 삭제
    public boolean deleteRegisterToken(String username) {
        return Optional.ofNullable(redisTemplate.hasKey(username))
                .filter(Boolean::booleanValue) // 키가 존재할 때만 진행
                .map(hasKey -> {
                    String accessToken = (String) redisTemplate.opsForValue().get(username);
                    redisTemplate.delete(accessToken);
                    redisTemplate.delete(username);
                    return true;
                })
                .orElse(false); // 키가 존재하지 않거나 삭제되지 않았을 때 false 반환
    }
}