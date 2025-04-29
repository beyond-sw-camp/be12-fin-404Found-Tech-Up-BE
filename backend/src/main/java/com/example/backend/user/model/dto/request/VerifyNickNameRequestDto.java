package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="회원가입시 닉네임 중복 확인 Request")
public class VerifyNickNameRequestDto {
    @Schema(description="별명, 필수",required = true,  example = "Yippie20")
    @NotBlank
    private String userNickname;
}
