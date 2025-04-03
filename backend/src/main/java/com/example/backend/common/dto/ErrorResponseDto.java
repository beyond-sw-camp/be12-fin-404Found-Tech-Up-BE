package com.example.backend.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="에러 발생시 응답 양식이 필요할 경우 사용하는 전송 객체")
public class ErrorResponseDto {
    @Schema(description = "오류 코드", examples={"400", "500"})
    private String code;
    @Schema(description = "오류 코드", example="오류가 발생했습니다")
    private String message;
}
