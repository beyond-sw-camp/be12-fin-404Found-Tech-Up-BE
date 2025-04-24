package com.example.backend.config.interceptor;

import com.example.backend.global.auth.StompPrincipal;
import com.example.backend.user.model.User;
import com.example.backend.util.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtility jwtUtility;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            HttpServletRequest servletRequest =
                    (HttpServletRequest) accessor.getSessionAttributes().get("HTTP_REQUEST");

            if (servletRequest != null) {
                Cookie cookie = WebUtils.getCookie(servletRequest, "ATOKEN");
                if (cookie != null) {
                    String token = cookie.getValue();
                    User user = JwtUtility.buildUserDataFromToken(token); // ← 기존 JwtFilter에서 하던 작업
                    if (user != null) {
                        String userIdx = user.getUserIdx().toString();
                        System.out.println("[🧩 WebSocket CONNECT] Principal 설정됨 → userIdx: " + userIdx);
                        accessor.setUser(new StompPrincipal(userIdx));}
                }
            }
        }

        return message;
    }
}
