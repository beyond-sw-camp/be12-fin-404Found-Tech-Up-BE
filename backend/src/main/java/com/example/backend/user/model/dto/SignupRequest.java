package com.example.backend.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private String validatedString;
    private String password;
    private Boolean agreement;
}
