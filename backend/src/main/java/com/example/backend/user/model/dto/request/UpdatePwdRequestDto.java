package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="사용자 비밀번호 변경 요청")
public class UpdatePwdRequestDto {
    @Schema(description="비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="wrong pass")
    private String userCurrentPassword;
    @Schema(description="비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="wrong pass")
    private String userPassword;
    @Schema(description="확인 비밀번호, 영문 소문자 및 숫자로 8자 이상, 필수",required = true,  example = "abcd142857")
    @Pattern(regexp = "[0-9a-z]{8,}", message="wrong pass")
    private String userConfirmPassword;
}
