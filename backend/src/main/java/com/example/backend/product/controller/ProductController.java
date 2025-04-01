package com.example.backend.product.controller;

import com.example.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 API", description = "상품 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 리스트 조회", description = "조건에 따라 상품 리스트를 조회합니다.")
    @GetMapping("/list")
    public void getProductList() {
        // TODO: 구현
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public void getProductDetail(@PathVariable Long productId) {
        // TODO: 구현
    }

    @Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다.")
    @GetMapping("/search")
    public void searchProduct(@RequestParam String keyword) {
        // TODO: 구현
    }

    @Operation(summary = "상품 필터링", description = "가격, 브랜드, 용량 등으로 상품을 필터링합니다.")
    @PostMapping("/filter")
    public void filterProduct() {
        // TODO: 구현
    }
}
