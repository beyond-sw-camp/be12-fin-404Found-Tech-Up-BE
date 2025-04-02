package com.example.backend.wishlist.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "위시리스트 등록/삭제 요청 DTO")
public class WishlistRequestDto {

    @Schema(description = "추가 또는 삭제할 상품의 고유 번호", example = "101", required = true)
    private Long productIdx;
}
