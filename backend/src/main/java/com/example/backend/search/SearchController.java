package com.example.backend.search;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ReducedProductResponseDto;
import com.example.backend.search.model.ProductIndexDocument;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "빠른 검색", description = "엘라스틱서치에 저장된 결과를 가져옵니다")
    @GetMapping
    public BaseResponse<List<ReducedProductResponseDto>> getSearchResult(@RequestParam String name, @RequestParam String category, @RequestParam Integer page, @RequestParam Integer size) {
        if (name == null || name.isEmpty()) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_NOT_FOUND);
        }
        if (page < 0 || size < 0) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_NOT_FOUND);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(searchService.searchByName(name, category, page, size), ProductResponseStatus.SUCCESS);
    }

    @Operation(summary="카테고리 없는 기본 검색")
    @GetMapping("/title-only")
    public BaseResponse<Page<ReducedProductResponseDto>> getSearchResultWithoutCategory(@RequestParam String name, @RequestParam Integer page, @RequestParam Integer size) {
        if (page < 0 || size < 0) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_NOT_FOUND);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(searchService.searchByName(name, PageRequest.of(page, size)), ProductResponseStatus.SUCCESS);
    }


}
