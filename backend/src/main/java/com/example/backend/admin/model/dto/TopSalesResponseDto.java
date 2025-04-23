package com.example.backend.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TopSalesResponseDto {
    private Long productIdx;
    private String productName;
    private Double productPrice;
    private String productImageUrl;
    private Integer productDiscount;
    private List<Integer> reviews;
}
