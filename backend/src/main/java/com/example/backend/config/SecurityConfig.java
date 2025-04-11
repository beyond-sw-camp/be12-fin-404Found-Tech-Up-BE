package com.example.backend.config;

import com.example.backend.config.filter.JwtFilter;
import com.example.backend.config.filter.LoginFilter;
import com.example.backend.config.filter.OAuth2SuccessHandler;
import com.example.backend.user.service.CustomOAuth2UserService;
import com.example.backend.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 기반 stateless API에 적합)
                .csrf(csrf -> csrf.disable())
                // 세션 비활성화 (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Cookie cookie = new Cookie("Authorization", null);
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                )
                // 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 공개 엔드포인트
                        .requestMatchers(
                                "/signup",
                                "/login",
                                "/oauth2/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // 나머지 요청은 인증 필요
                        .anyRequest().permitAll()
                )
                // OAuth2 로그인 설정
                .oauth2Login(config -> {
                    config.successHandler(new OAuth2SuccessHandler());
                    config.userInfoEndpoint(endpoint ->
                            endpoint.userService(customOAuth2UserService));
                })
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService)
//                        )
//                        .successHandler(oAuth2SuccessHandler)
//                        .failureHandler((request, response, exception) -> {
//                            log.error("OAuth2 failure: ", exception);
//                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                            response.setContentType("application/json");
//                            response.getWriter().write("{\"error\": \"OAuth2 login failed: " + exception.getMessage() + "\"}");
//                        })
//                )
                // 필터 추가
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
