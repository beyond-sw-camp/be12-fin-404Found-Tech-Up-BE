package com.example.backend.search;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ReducedProductResponseDto;
import com.example.backend.search.model.ProductIndexDocument;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public BaseResponse<Page<ReducedProductResponseDto>> getSearchResult(@RequestParam String name, @RequestParam String category, @RequestParam Double priceLow, @RequestParam Double priceHigh, @RequestParam Integer page, @RequestParam Integer size) {
        if (name == null || name.isEmpty()) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_NOT_FOUND);
        }
        if (page < 0 || size < 0) {
            return new BaseResponseServiceImpl().getFailureResponse(ProductResponseStatus.PRODUCT_NOT_FOUND);
        }
        if (category == null || category.isEmpty()) {
            return new BaseResponseServiceImpl().getSuccessResponse(searchService.searchByNameAndPriceRange(name, priceLow, priceHigh, PageRequest.of(page, size)), ProductResponseStatus.SUCCESS);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(searchService.searchByNameAndCategoryAndPriceRange(name, category, priceLow, priceHigh, PageRequest.of(page, size)), ProductResponseStatus.SUCCESS);
    }

    @PutMapping
    public BaseResponse<Object> updateSearchResult(/*@AuthenticationPrincipal User user*/) {
        /*
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        */
        searchService.createIndex();
        return new BaseResponseServiceImpl().getSuccessResponse(CommonResponseStatus.SUCCESS);
    }
}
