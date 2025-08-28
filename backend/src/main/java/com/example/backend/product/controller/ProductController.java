package com.example.backend.product.controller;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.*;
import com.example.backend.product.service.ProductService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "상품 기능", description = "상품 관련 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "상품 리스트 조회 (paged)", description = "페이지 단위로 상품 리스트를 조회합니다.")
    @GetMapping("/list")
    public BaseResponse<Page<ProductResponseDto>> getProductList(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("listing category {}", category);
        if (category == null || category.isBlank() || category.equals("ALL")) {
            Page<ProductResponseDto> dtos = productService.getProductList(pageable);
            return baseResponseService.getSuccessResponse(dtos, ProductResponseStatus.SUCCESS);
        } else {
            Page<ProductResponseDto> dtos = productService.getProductList(category, pageable);
            return baseResponseService.getSuccessResponse(dtos, ProductResponseStatus.SUCCESS);
        }
    }
    /*
    @Operation(summary = "상품 리스트 조회 (스펙 포함, paged)", description = "페이지 단위로 스펙을 포함하여 상품 리스트를 조회합니다.")
    @GetMapping("/list/full")
    public BaseResponse<Page<ProductResponseDto>> getProductFullSpecList(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("listing category {}", category);
        if (category == null || category.isBlank() || category.equals("ALL")) {
            Page<ProductResponseDto> dtos = productService.getProductListWithSpec(pageable);
            return baseResponseService.getSuccessResponse(dtos, ProductResponseStatus.SUCCESS);
        } else {
            Page<ProductResponseDto> dtos = productService.getProductListWithSpec(category, pageable);
            return baseResponseService.getSuccessResponse(dtos, ProductResponseStatus.SUCCESS);
        }
    }
    */
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public BaseResponse<ProductResponseDto> getProductDetail(@PathVariable Long productId) {
        ProductResponseDto response = productService.getProductDetail(productId);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "상품 검색",
            description = "이름에 특정 키워드가 포함된 카테고리별 상품을 검색합니다.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "keyword",
                            description = "상품 이름에 포함된 키워드 (예: i7)",
                            example = "i7"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "category",
                            description = "상품 분류",
                            example = "CPU"
                    )
            }
    )

    @GetMapping("/search")
    public BaseResponse<Page<ProductResponseDto>> searchProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> results = productService.searchProduct(keyword, category, pageable);
        return baseResponseService.getSuccessResponse(results, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 필터링", description = "카테고리, 이름 키워드, 가격 범위 등의 조건으로 상품을 필터링합니다.")
    @PostMapping("/filter")
    public BaseResponse<Page<ProductResponseDto>> filterProduct(
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
            @RequestBody ProductFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return baseResponseService.getSuccessResponse(productService.filterProduct(filterDto, PageRequest.of(page,size)), ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "Content-based recommendations")
    @PostMapping("/recommend/content-based")
    public BaseResponse<List<ProductResponseDto>> recommendContentBased(
            @RequestBody ProductRecommendRequestDto request
    ) {
        List<ProductResponseDto> recs =
                productService.getContentBasedRecommendations(request.getProductIdx(), request.getResultNum());
        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "Item-based recommendations")
    @PostMapping("/recommend/item-based")
    public BaseResponse<List<ProductResponseDto>> recommendItemBased(
            @RequestBody ProductRecommendRequestDto request
    ) {
        List<ProductResponseDto> recs =
                productService.getItemBasedRecommendations(request.getProductIdx(), request.getResultNum());
        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "User-based recommendations")
    @PostMapping("/recommend/user-based")
    public BaseResponse<List<ProductResponseDto>> recommendUserBased(
            @AuthenticationPrincipal User user, @RequestBody ProductRecommendUserRequestDto request
    ) {
        List<ProductResponseDto> recs =
                productService.getUserBasedRecommendations(user.getUserIdx(), request.getResultNum());
        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
    }

    //-----------------------관리자 전용 상품 기능----------------

    @Operation(summary = "상품 등록", description = "신규 상품을 등록합니다.")
    @PostMapping("/register")
    public BaseResponse<ProductResponseDto> registerProduct(@AuthenticationPrincipal User user, @RequestBody ProductRequestDto requestDto) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
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
    public BaseResponse<ProductDeleteResponseDto> deleteProduct(@AuthenticationPrincipal User user, @PathVariable Long productId) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        ProductDeleteResponseDto response = productService.deleteProduct(productId);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 수정", description = "상품 ID를 기준으로 상품 정보를 수정합니다.")
    @PutMapping("/update/{productId}")
    public BaseResponse<ProductResponseDto> updateProduct(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestBody ProductRequestDto requestDto
    ) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        ProductResponseDto response = productService.updateProduct(productId, requestDto);
        return baseResponseService.getSuccessResponse(response, ProductResponseStatus.SUCCESS);
    }

}
