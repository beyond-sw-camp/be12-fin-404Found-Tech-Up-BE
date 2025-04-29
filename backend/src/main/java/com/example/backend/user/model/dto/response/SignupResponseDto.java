package com.example.backend.user.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="회원가입 성공 여부 Response")
public class SignupResponseDto {
    @Schema(description="회원가입 성공 여부")
    private boolean successSignup;
}
