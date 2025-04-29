package com.example.backend.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserUpdateRequestDto {
    @Schema(description="전화번호, 패턴에 맞아야 함",required = true,  example = "010-1234-5678")
    @Pattern(regexp = "010-[0-9]{4}-[0-9]{4}")
    private String userPhone;
    @Schema(description="주소, 회원 정보 변경 시에는 필수 입력", required = true,  example = "서울특별시 종로구 청와대로 1")
    @NotBlank
    private String userAddress;
}
