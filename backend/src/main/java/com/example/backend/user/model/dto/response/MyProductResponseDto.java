package com.example.backend.user.model.dto.response;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.ProductResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="사용자 제품 목록 Response")
public class MyProductResponseDto {
    private Long userIdx;
    private List<ProductResponseDto> products;
}
