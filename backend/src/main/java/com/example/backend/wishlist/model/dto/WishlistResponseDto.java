package com.example.backend.wishlist.model.dto;

import com.example.backend.common.dto.ProductSummaryDto;
import com.example.backend.wishlist.model.Wishlist;
import com.example.backend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "위시리스트 응답 DTO")
public class WishlistResponseDto {

    @Schema(description = "위시리스트 항목 고유 번호", example = "1")
    private Long wishlistIdx;

    @Schema(description = "상품 요약 정보")
    private ProductSummaryDto product;

    public static WishlistResponseDto from(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .wishlistIdx(wishlist.getWishlistIdx())
                .product(ProductSummaryDto.from(wishlist.getProduct()))
                .build();
    }

}
