package com.example.backend.product.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductRecommendRequestDto {
    @JsonProperty("product_idx")
    private Long productIdx;
    @JsonProperty("result_num")
    private Integer resultNum;
}