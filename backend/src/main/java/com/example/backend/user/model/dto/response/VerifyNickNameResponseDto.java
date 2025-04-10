package com.example.backend.user.model.dto.response;

import com.example.backend.board.model.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="사용자 닉네임 중복 여부 Response")
public class VerifyNickNameResponseDto {
    @Schema(description="중복 확인 여부")
    private boolean verifyNickname;
}
