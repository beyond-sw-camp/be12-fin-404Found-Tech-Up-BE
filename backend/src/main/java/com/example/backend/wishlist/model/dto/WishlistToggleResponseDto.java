package com.example.backend.wishlist.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위시리스트 토글 응답 DTO")
public class WishlistToggleResponseDto {
    @Schema(description = "토글 대상 상품의 고유번호", example = "1")
    private Long productIdx;
    @Schema(description = "토글 결과 메시지", example = "상품을 위시리스트에 추가했습니다.")
    private String message;

    public static WishlistToggleResponseDto from(Long productIdx, String message) {
        return WishlistToggleResponseDto.builder()
                .productIdx(productIdx)
                .message(message)
                .build();
    }
}
