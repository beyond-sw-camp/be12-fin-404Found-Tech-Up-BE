package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="비밀번호 찾기 요청")
public class EditPwdRequestDto {
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
}
