package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.review.model.Review;
import com.example.backend.search.model.ProductIndexDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private String image;

    public static ReducedProductResponseDto from(ProductIndexDocument product) {
        return ReducedProductResponseDto.builder()
                .idx(product.getProductIdx())
                .name(product.getProductName())
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