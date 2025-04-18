package com.example.backend.user.model.dto.request;

import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="회원가입 정보 Request")
public class SignupRequestDto {
    @Schema(description="별명, 필수",required = true,  example = "Yippie20")
    @NotBlank
    private String userNickname;
    @Schema(description="별명 중복 확인 여부, 필수",required = true,  example = "true")
    @NotBlank
    private Boolean verifyNickname;
    @Schema(description="이메일, 필수",required = true,  example = "example@example.com")
    @Email
    @NotBlank
    private String userEmail;
    @Schema(description="이메일 인증 코드, 필수",required = true,  example = "123456")
    @NotBlank
    private String inputCode;
    @Schema(description="비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="signup wrong pass")
    private String userPassword;
    @Schema(description="확인 비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="signup wrong pass")
    private String userConfirmPassword;
//    @Schema(description="약관 동의 기록, 참이어야 회원 가입 성공 처리", required = true, example = "true")
//    @NotNull
//    private Boolean agreement;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .userNickname(userNickname)
                .userEmail(userEmail)
                .userPassword(encodedPassword)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .isAdmin(false)
                .likeEnabled(false)
                .newEnabled(false)
                .upgradeEnabled(false)
                .build();
    }
}
