package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductResponseDto {
    private int idx;
    private String name;
    private double price;
    private String brand;
    private int stock;
    private String description;
    private String category;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .idx(product.getProductIdx())
                .name(product.getName())
                .price(product.getPrice())
                .brand(product.getBrand())
                .stock(product.getStock())
                .description(product.getDescription())
                .category(product.getCategory())
                .build();
    }
}
