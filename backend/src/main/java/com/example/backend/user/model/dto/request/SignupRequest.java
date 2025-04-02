package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="회원가입 정보 Request")
public class SignupRequest {
    @Schema(description="별명, 필수",required = true,  example = "Yippie20")
    @NotBlank
    private String nickname;
    @Schema(description="이메일, 필수",required = true,  example = "example@example.com")
    @Email
    @NotBlank
    private String email;
    @Schema(description="이메일 인증으로 받은 고유 문자열, 필수",required = true,  example = "142857")
    @NotBlank
    private String validatedString;
    @Schema(description="비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="signup wrong pass")
    private String password;
    @Schema(description="약관 동의 기록, 참이어야 회원 가입 성공 처리", required = true, example = "true")
    @NotNull
    private Boolean agreement;
}
