package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public class ChangePasswordRequestDto {
    @Schema(description="옛 비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="signup wrong pass")
    private String oldPassword;
    @Schema(description="새 비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="signup wrong pass")
    private String newPassword;
}
