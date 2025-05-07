package com.example.backend.product.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentRecommendResponseDto {
    @JsonProperty("similar_products")
    private List<SimilarProduct> similarProducts;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarProduct {
        @JsonProperty("product_idx")
        private Long productIdx;
    }
}
