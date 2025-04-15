package com.example.backend.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 삭제 응답 DTO")
public class ProductDeleteResponseDto {
    @Schema(description = "삭제된 상품의 아이디", example = "1")
    private Long productIdx;

    public static ProductDeleteResponseDto from(Long productIdx) {
        return ProductDeleteResponseDto.builder()
                .productIdx(productIdx)
                .build();
    }
}
