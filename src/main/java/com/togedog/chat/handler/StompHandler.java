package com.togedog.chat.handler;

import com.togedog.auth.jwt.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenizer jwtTokenizer;


//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor.getCommand() == StompCommand.CONNECT) {
//            String accessToken = accessor.getFirstNativeHeader("Authorization");
//            if (accessToken != null && JwtTokenizer.validateToken(accessToken)) {
//                Authentication authentication = JwtTokenizer.getAuthentication(accessToken);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                throw new AuthenticationCredentialsNotFoundException("AccessToken is null or not valid");
//            }
//        }
//        return message;
//    }
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (accessor.getCommand() == StompCommand.CONNECT) {
//            if (!this.validateAccessToken(accessor.getFirstNativeHeader("Authorization"))) {
//                throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
//            }
//        }
//        return message;
//    }

    private boolean validateAccessToken(String accessToken) {

        if (accessToken == null) {
            return false;
        }

        String bearerToken = accessToken.trim();

        if (!bearerToken.trim().isEmpty() && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);

            try {

                String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
                Claims claims = JwtTokenizer.getClaimsFromAccessToken(accessToken,base64EncodedSecretKey);
                return true;
            } catch (ExpiredJwtException | MalformedJwtException e) {
                return false;
            }
        }

        return false;
    }
}
