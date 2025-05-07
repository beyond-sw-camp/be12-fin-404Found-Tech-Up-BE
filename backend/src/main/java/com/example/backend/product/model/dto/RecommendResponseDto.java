package com.example.backend.product.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendResponseDto {

    @JsonProperty("recommended_products")
    private List<RecommendedProduct> recommendedProducts;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedProduct {
        @JsonProperty("product_idx")
        private Long productIdx;

//        @JsonProperty("similarity")
//        private Double similarity;
    }
}
