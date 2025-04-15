package com.example.backend.user.service;

import com.example.backend.user.model.CustomOAuth2User;
import com.example.backend.user.model.User;
import com.example.backend.user.model.dto.response.SignupResponseDto;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 카카오 API에서 사용자 정보 가져오기
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 사용자 정보 추출
        String kakaoId = String.valueOf(attributes.get("id"));
        String email = "kakao_" + kakaoId + "@example.com";
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;

        // 닉네임 중복 방지를 위해 고유 닉네임 생성
        String finalNickname = generateUniqueNickname(nickname, kakaoId);

        // 사용자 조회 또는 생성
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    // 새로운 사용자 생성
                    User newUser = User.builder()
                            .kakaoId(kakaoId)
                            .userEmail(email)
                            .userNickname(finalNickname)
                            .isSocial("Kakao")
                            .enabled(true)
                            .isAdmin(false)
                            .isActive(false)
                            .createdAt(LocalDateTime.now())
                            .likeEnabled(false)
                            .newEnabled(false)
                            .upgradeEnabled(false)
                            .build();
                    return userRepository.save(newUser);
                });

        // CustomOAuth2User 반환
        return new CustomOAuth2User(user, attributes);
        }

    // 닉네임 중복 방지 로직
    private String generateUniqueNickname(String baseNickname, String kakaoId) {
        String nickname = baseNickname != null ? baseNickname : "kakao_" + kakaoId;
        int suffix = 1;
        String finalNickname = nickname;

        // 닉네임 중복 확인
        while (userRepository.findByUserNickname(finalNickname).isPresent()) {
            finalNickname = nickname + "_" + suffix;
            suffix++;
        }
        return finalNickname;
    }
}
