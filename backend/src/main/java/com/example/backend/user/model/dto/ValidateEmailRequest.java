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
@Schema(description="이메일 인증 요청")
public class ValidateEmailRequest {
    @Schema(description="이메일, 필수",required = true,  example = "example@example.com")
    @Email
    @NotBlank
    private String email;
}
