package com.example.backend.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductFilterResponseDto {
    List<ProductResponseDto> content;
    Long totalElements;
}
