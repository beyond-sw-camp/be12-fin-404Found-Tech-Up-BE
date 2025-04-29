package com.example.backend.config.filter;

import com.example.backend.user.model.CustomOAuth2User;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.util.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.time.Duration;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final JwtUtility jwtUtility;
//
//    public OAuth2SuccessHandler(JwtUtility jwtUtility) {
//        this.jwtUtility = jwtUtility;
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        String jwtToken = JwtUtility.generateToken(user.getIdx(), user.getEmail(), false);

        // 쿠키에 토큰 설정
        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1))
                .build();

        // JSESSIONID 쿠키 무효화
        ResponseCookie jsessionIdCookie = ResponseCookie
                .from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0) // 즉시 만료
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jsessionIdCookie.toString());

        // 원래 페이지 리다이렉트
        String state = request.getParameter("state");
        String redirectUrl = "https://techup-inner.p-e.kr";
//        String redirectUrl = "http://localhost:3000";
        if (state != null && !state.isEmpty()) {
            try {
                String decodedUrl = URLDecoder.decode(state, "UTF-8");
                if (decodedUrl.startsWith("https://techup-inner.p-e.kr")) {
//                if (decodedUrl.startsWith("http://localhost:3000")) {
                    redirectUrl = decodedUrl;
                    log.info("Redirecting to original URL: {}", redirectUrl);
                } else {
                    log.warn("Invalid redirect URL: {}", decodedUrl);
                }
            } catch (Exception e) {
                log.error("Failed to decode state parameter: {}", state, e);
            }
        } else {
            log.warn("No state parameter found, using default redirect: {}", redirectUrl);
        }

        // 세션 무효화 (추가 안전 조치)
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
            log.info("Invalidated existing session");
        }

        response.sendRedirect(redirectUrl);
    }
}
