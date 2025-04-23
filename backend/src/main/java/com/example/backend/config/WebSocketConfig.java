package com.example.backend.config;

import com.example.backend.config.interceptor.JwtHandshakeInterceptor;
import com.example.backend.global.auth.StompPrincipal;
import com.example.backend.user.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String WS_USER_KEY = "user"; // 🔐 SessionAttributes 키 통일

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor()) // 🔹 쿠키 기반 사용자 정보 주입
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 🔔 클라이언트 구독용 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 📨 클라이언트 → 서버 메시지 prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Object userAttr = accessor.getSessionAttributes().get(WS_USER_KEY);
                    if (userAttr instanceof User user) {
                        accessor.setUser(new StompPrincipal(user.getUserIdx().toString())); // Principal 설정
                    } else {
                        accessor.setUser(null);
                    }
                }

                return message;
            }
        });
    }
}
