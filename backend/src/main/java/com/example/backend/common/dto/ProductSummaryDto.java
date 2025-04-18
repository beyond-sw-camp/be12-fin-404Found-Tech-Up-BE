package com.example.backend.common.dto;

import com.example.backend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 장바구니, 위시리스트에서 필요한 상품 정보만 담겨있는
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 요약 정보 DTO")
public class ProductSummaryDto {

    @Schema(description = "상품 고유 번호", example = "101")
    private Long productIdx;

    @Schema(description = "상품 이름", example = "Example Product")
    private String name;

    @Schema(description = "상품 가격", example = "50000")
    private double price;

    @Schema(description = "할인율", example = "10")
    private Integer discount;

    @Schema(description = "상품 브랜드", example = "BrandX")
    private String brand;

    @Schema(description = "상품 재고", example = "120")
    private Integer stock;

    // 첫번째로 등록된 사진 1개만 장바구니에서 보여줌
    @Schema(description = "상품 썸네일 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    public static ProductSummaryDto from(Product product) {
        // 제품 이미지가 존재하면 첫 번째 이미지의 URL을 사용
        String imageUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            // ProductImage 엔티티에 getImageUrl() 메소드가 있다고 가정
            imageUrl = product.getImages().get(0).getImageUrl();
        }
        return ProductSummaryDto.builder()
                .productIdx(product.getProductIdx())
                .name(product.getName())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .brand(product.getBrand())
                .stock(product.getStock())
                .imageUrl(imageUrl)
                .build();
    }
}
