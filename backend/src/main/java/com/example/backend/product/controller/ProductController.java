package com.example.backend.product.controller;

import com.example.backend.product.model.dto.ProductFilterRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품 API", description = "상품 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 리스트 조회", description = "전체 상품 리스트를 조회합니다.")
    @GetMapping("/list")
    public List<ProductResponseDto> getProductList() {
        return productService.getProductList();
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ProductResponseDto getProductDetail(
            @PathVariable Long productId
    ) {
        return productService.getProductDetail(productId);
    }

    @Operation(
            summary = "상품 검색",
            description = "이름에 특정 키워드가 포함된 상품을 검색합니다.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "keyword",
                            description = "상품 이름에 포함된 키워드 (예: i7)",
                            example = "i7"
                    )
            }
    )
    @GetMapping("/search")
    public List<ProductResponseDto> searchProduct(@RequestParam String keyword) {
        return productService.searchProduct(keyword);
    }

    @Operation(summary = "상품 필터링", description = "카테고리, 이름 키워드, 가격 범위 등의 조건으로 상품을 필터링합니다.")
    @PostMapping("/filter")
    public List<ProductResponseDto> filterProduct(
            @RequestBody(
                    description = "필터링 조건을 담은 JSON 객체",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProductFilterRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "CPU i7 필터링 예시",
                                            summary = "CPU 제품 중 이름에 'i7'이 포함된 상품을 필터링",
                                            value = """
                                                    {
                                                      "category": "CPU",
                                                      "nameKeyword": "i7"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "브랜드 + 가격 범위 필터링",
                                            summary = "삼성 브랜드의 RAM 제품 중 가격이 50000원 이상 100000원 이하",
                                            value = """
                                                    {
                                                      "brand": "Samsung",
                                                      "category": "RAM",
                                                      "minPrice": 50000,
                                                      "maxPrice": 100000
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            ProductFilterRequestDto filterDto
    ) {
        return productService.filterProduct(filterDto);
    }
}
