package com.example.backend.user.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private User user;
    private final Map<String, Object> attributes; // 카카오 원본 속성

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 카카오 속성 반환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        authorities.add(authority);
        return authorities;
    }

    @Override
    public String getName() {
        return user.getKakaoId(); // 카카오 ID 반환
    }

    public Long getIdx() {
        return user.getUserIdx();
    }

    public String getEmail() {
        return user.getUserEmail();
    }
}
