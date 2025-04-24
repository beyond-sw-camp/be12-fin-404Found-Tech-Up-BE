package com.example.backend.config;

import com.example.backend.config.interceptor.JwtHandshakeInterceptor;
import com.example.backend.config.interceptor.JwtChannelInterceptor;
import com.example.backend.config.interceptor.CustomHandshakeHandler;
import com.example.backend.util.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtility jwtUtility; // 🔐 JwtUtility 주입
    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler(jwtUtility)) // ✅ 여기!
                .withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor); // 사용 중이면 유지
    }
}
