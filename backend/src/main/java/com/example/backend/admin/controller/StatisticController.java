package com.example.backend.admin.controller;

import com.example.backend.admin.model.dto.StatisticsResponseDto;
import com.example.backend.admin.model.dto.TopSalesResponseDto;
import com.example.backend.admin.model.dto.TopWishlistResponseDto;
import com.example.backend.admin.service.StatisticsService;
import com.example.backend.common.dto.ErrorResponseDto;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/statistics")
public class StatisticController {
    private final StatisticsService statisticsService;

    @Operation(summary="관리자 대시보드용 통계", description = "접속한 관리자 유저에게 대시보드 통계를 반환합니다.")
    @GetMapping
    public BaseResponse<StatisticsResponseDto> etcStatistics(@AuthenticationPrincipal User user) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getStatisticsEtc(), CommonResponseStatus.SUCCESS);
    }


    @Operation(summary="상위 위시리스트 제품 목록", description = "상위 위시리스트 확인")
    @ApiResponse(responseCode="200", description="가져옴", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/wishlist")
    public BaseResponse<List<TopWishlistResponseDto>> getTopWishList() {
        return new BaseResponseServiceImpl().getSuccessResponse( statisticsService.getTopWishList(), CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="총 환불 수", description = "총 환불 수를 정수로 반환합니다.")
    @GetMapping("/refund")
    public BaseResponse<Integer> getTotalRefund() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getTotalRefunds(), CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="총 주문 수", description = "총 주문 수를 정수로 반환합니다")
    @GetMapping("/order")
    public BaseResponse<Integer> getTotalOrder() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getTotalOrders(), CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="상위 판매량 목록", description = "상위 판매량 목록")
    @ApiResponse(responseCode="200", description="상위 판매량 목록.", content= @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode="400", description="실패", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @ApiResponse(responseCode="500", description="서버 내 오류", content= @Content(schema = @Schema(implementation = ErrorResponseDto.class), mediaType = "application/json"))
    @GetMapping("/topsales")
    public BaseResponse<List<TopSalesResponseDto>> getTopSales() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getTopSales(), CommonResponseStatus.SUCCESS);
    }

    @Operation(summary="상위 매출 목록", description = "상위 매출 목록을 정수 리스트로 반환합니다.")
    @GetMapping("/incomes")
    public BaseResponse<List<Integer>> getTopIncomes() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getRecentEarningList(), CommonResponseStatus.SUCCESS);
    }
}
