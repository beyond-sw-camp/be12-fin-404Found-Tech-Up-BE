package com.example.backend.config.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("[Handshake] beforeHandshake 진입");

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 쿠키 로그
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                log.info("[Handshake] 쿠키 수신: {}", Arrays.toString(cookies));
            } else {
                log.warn("[Handshake] 쿠키 없음");
            }

            // 헤더 로그
            String origin = httpRequest.getHeader("Origin");
            String userAgent = httpRequest.getHeader("User-Agent");
            log.info("[Handshake] Origin: {}", origin);
            log.info("[Handshake] User-Agent: {}", userAgent);

            attributes.put("HTTP_REQUEST", httpRequest); // ChannelInterceptor에서 접근 가능
            log.info("[Handshake] HTTP_REQUEST 세션에 저장 완료");
        } else {
            log.warn("[Handshake] 요청이 ServletServerHttpRequest 타입이 아님");
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("[Handshake] afterHandshake 호출됨");
    }
}
