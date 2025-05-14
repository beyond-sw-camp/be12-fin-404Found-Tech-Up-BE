package com.example.backend.es.controller;

import com.example.backend.es.model.EsEntity;
import com.example.backend.es.service.EsService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.dto.ProductRecommendRequestDto;
import com.example.backend.product.model.dto.ProductRecommendUserRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "ES 추천 기능", description = "ES 이용 추천 기능을 제공합니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/es")
public class EsController {
    private final EsService esService;
    private final BaseResponseService baseResponseService;

//    @Operation(summary = "Content-based recommendations")
//    @PostMapping("/recommend/content-based")
//    public BaseResponse<List<ProductResponseDto>> recommendContentBased(
//            @RequestBody ProductRecommendRequestDto request
//    ) {
//        List<ProductResponseDto> recs =
//                esService.getContentBasedRecommendations(request.getProductIdx(), request.getResultNum());
//        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
//    }

    @Operation(summary = "Item-based recommendations")
    @PostMapping("/recommend/item-based")
    public BaseResponse<List<ProductResponseDto>> recommendItemBased(
            @RequestBody ProductRecommendRequestDto request
    ) {
        List<ProductResponseDto> recs =
                esService.getItemBasedRecommendations(request.getProductIdx(), request.getResultNum());
        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
    }

    @Operation(summary = "User-based recommendations")
    @PostMapping("/recommend/user-based")
    public BaseResponse<List<EsEntity>> recommendUserBased(
            @AuthenticationPrincipal User user, @RequestBody ProductRecommendUserRequestDto request
    ) {
        List<EsEntity> recs =
                esService.getUserBasedRecommendations(user.getUserIdx(), request.getResultNum());
        return baseResponseService.getSuccessResponse(recs, ProductResponseStatus.SUCCESS);
    }
}
