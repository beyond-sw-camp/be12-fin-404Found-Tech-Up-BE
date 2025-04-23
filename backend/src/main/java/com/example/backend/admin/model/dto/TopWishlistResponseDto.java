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
public class TopWishlistResponseDto {
    private Long productIdx;
    private String productName;
    private String brand;
    private Double price;
    private String imageUrl;
    private Integer productDiscount;
    private List<Integer> reviews; // reviews
    private Integer cw; // count wishlist
}
