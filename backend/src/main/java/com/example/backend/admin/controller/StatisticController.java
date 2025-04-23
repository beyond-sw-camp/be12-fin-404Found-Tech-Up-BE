package com.example.backend.admin.controller;

import com.example.backend.admin.model.dto.StatisticsResponseDto;
import com.example.backend.admin.model.dto.TopSalesResponseDto;
import com.example.backend.admin.model.dto.TopWishlistResponseDto;
import com.example.backend.admin.service.StatisticsService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.user.model.User;
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

    @GetMapping
    public BaseResponse<StatisticsResponseDto> statistics(@AuthenticationPrincipal User user) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getStatistics(), CommonResponseStatus.SUCCESS);
    }

    // ---- 이 아래는 테스트용 api로 규칙을 따르지 않음----

    @GetMapping("/wishlist")
    public BaseResponse<List<TopWishlistResponseDto>> getTopWishList() {
        return new BaseResponseServiceImpl().getSuccessResponse( statisticsService.getTopWishList(), CommonResponseStatus.SUCCESS);
    }
    /*
    @GetMapping("/refund")
    public ResponseEntity<Integer> getTotalRefund() {
        return ResponseEntity.ok(statisticsService.getTotalRefunds());
    }
    */
    @GetMapping("/order")
    public ResponseEntity<Integer> getTotalOrder() {
        return ResponseEntity.ok(statisticsService.getTotalOrders());
    }

    @GetMapping("/topsales")
    public BaseResponse<List<TopSalesResponseDto>> getTopSales() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getTopSales(), CommonResponseStatus.SUCCESS);
    }

    @GetMapping("/incomes")
    public BaseResponse<List<Integer>> getTopIncomes() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getRecentEarningList(), CommonResponseStatus.SUCCESS);
    }
}
