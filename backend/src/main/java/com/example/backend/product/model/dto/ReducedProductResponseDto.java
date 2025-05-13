package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.review.model.Review;
import com.example.backend.search.model.ProductIndexDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "엘라스틱서치에 저장된 상품 정보 목록 반환")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReducedProductResponseDto {
    private Long idx;
    private String name;
    private double price;
    private Integer discount;
    private String brand;
    private Integer stock;
    private String description;
    private String category;
    private Double rating;

    @Schema(description = "등록한 첫번째 이미지를 불러옵니다. 앞에 http: 또는 https:가 생략되어 있으니 주의해야 합니다.", example="//image.danawa.com/12")
    private String image;

    public static ReducedProductResponseDto from(ProductIndexDocument product) {
        return ReducedProductResponseDto.builder()
                .idx(product.getProductidx())
                .name(product.getProductname())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .brand(product.getBrand())
                .stock(product.getStock())
                .description(product.getDescription())
                .category(product.getCategory())
                .rating(product.getRating())
                .image(product.getImage())
                .build();
    }
}