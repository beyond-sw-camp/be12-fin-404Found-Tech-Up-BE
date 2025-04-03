package com.example.backend.config.filter;

import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.request.LoginRequestDto;
import com.example.backend.util.JwtUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // logger.info("누군가 로그인을 시도함");
        UsernamePasswordAuthenticationToken token;
        try {
            LoginRequestDto user = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            // logger.info("로그인 유저의 IP: {}", request.getRemoteAddr());
            token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        User user = (User) auth.getPrincipal();
        String jwt = JwtUtility.generateToken(user.getUserIdx(), user.getUserEmail(), user.getIsAdmin());
        // logger.info("{}님에게 {} JWT 토큰 부여", user.getEmail(), jwt);
        ResponseCookie cookie = ResponseCookie.from("ATOKEN", jwt)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(3600) // 1시간(3600초) 유효시간
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setContentType("application/json");

        response.getWriter().write("{ \"result\": \"로그인 성공\" }");
    }
}
