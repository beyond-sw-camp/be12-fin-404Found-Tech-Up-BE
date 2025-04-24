package com.example.backend.config.interceptor;

import com.example.backend.global.auth.StompPrincipal;
import com.example.backend.user.model.User;
import com.example.backend.util.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.WebUtils;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtility jwtUtility;

    public CustomHandshakeHandler(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Cookie tokenCookie = WebUtils.getCookie(httpServletRequest, "ATOKEN");
            if (tokenCookie != null) {
                String token = tokenCookie.getValue();
                User user = JwtUtility.buildUserDataFromToken(token);
                if (user != null) {
                    System.out.println("[🧩 Principal 설정됨] userIdx={}" + user.getUserIdx());
                    return new StompPrincipal(user.getUserIdx().toString());
                }
            }
        }
        return null; // 인증 실패 시
    }
}


