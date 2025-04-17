package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="이메일 인증 Request")
public class EmailRequestDto {
    @Schema(description="이메일, 필수",required = true,  example = "example@example.com")
    @Email
    @NotBlank
    private String userEmail;
    @Schema(description="회원가입, 비밀번호 찾기 여부(True: 회원가입, False: 비밀번호 찾기, 필수",required = true,  example = "true")
    @NotBlank
    private Boolean isSignup;
}
