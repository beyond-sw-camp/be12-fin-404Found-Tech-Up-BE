package com.example.backend.product.controller;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ProductDeleteResponseDto;
import com.example.backend.product.model.dto.ProductFilterRequestDto;
import com.example.backend.product.model.dto.ProductRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "상품 기능", description = "상품 관련 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "상품 리스트 조회", description = "전체 상품 리스트를 조회합니다.")
    @GetMapping("/list")
    public BaseResponse<List<ProductResponseDto>> getProductList() {
        List<ProductResponseDto> list = productService.getProductList();
        return baseResponseService.getSuccessResponse(list, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public BaseResponse<ProductResponseDto> getProductDetail(@PathVariable Long productId) {
        ProductResponseDto response = productService.getProductDetail(productId);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
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
    public BaseResponse<List<ProductResponseDto>> searchProduct(@RequestParam String keyword) {
        List<ProductResponseDto> list = productService.searchProduct(keyword);
        return baseResponseService.getSuccessResponse(list, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 필터링", description = "카테고리, 이름 키워드, 가격 범위 등의 조건으로 상품을 필터링합니다.")
    @PostMapping("/filter")
    public List<ProductResponseDto> filterProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
        List<ProductResponseDto> list = productService.filterProduct(filterDto);
        return baseResponseService.getSuccessResponse(list, ProductResponseStatus.SUCCESS);
    }

    //-----------------------관리자 전용 상품 기능----------------

    @Operation(summary = "상품 등록", description = "신규 상품을 등록합니다.")
    @PostMapping("/register")
    public BaseResponse<ProductResponseDto> registerProduct(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto response = productService.registerProduct(requestDto);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);

    }


    @Operation(summary = "상품 삭제", description = "상품 ID로 상품을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상품 ID"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @DeleteMapping("/{productId}")
    public BaseResponse<ProductDeleteResponseDto> deleteProduct(@PathVariable Long productId) {
        ProductDeleteResponseDto response = productService.deleteProduct(productId);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 수정", description = "상품 ID를 기준으로 상품 정보를 수정합니다.")
    @PutMapping("/update/{productId}")
    public BaseResponse<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto response = productService.updateProduct(productId, requestDto);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
    }

}
