package com.togedog.chat.config;

import com.togedog.chat.handler.StompHandler;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    /**
    * WebSocket을 통하여 Stomp를 사용할 Endpoint 설정 및 cors허가 권한
    *
    * @param StompEndpointRegistry
    * @author Tizesin(신민준)
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url = ws://localhost:8080/ws
        registry.addEndpoint("/ws") // 연결될 엔드포인트
                .setAllowedOrigins("*");
    }

    /**
    * 내부적으로 사용할 메시지 브로커에 관한 URL 설정
    *
    * @param MessageBrokerRegistry
    * @author Tizesin(신민준)
    */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지 구독 요청 url
        registry.enableSimpleBroker("/sub");
        //메시지 발행 요청 url
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /**
    * 클라이언트로부터 들어오는 메시지를 인터셉트 하여 인증절차 진행하는 메서드
    * 사용할려면은 사용자 인증 정보를 가지고 있는 redis cashMemory 를 사용해야함..
    *
    * @param ChannelRegistration
    * @throws ExpiredJwtException / MalformedJwtException
    * @author Tizesin(신민준)
    */
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompHandler);
//    }
}
