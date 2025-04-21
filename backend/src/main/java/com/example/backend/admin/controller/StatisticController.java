package com.example.backend.admin.controller;

import com.example.backend.admin.model.StatisticsResponseDto;
import com.example.backend.admin.model.TopSales;
import com.example.backend.admin.model.TopWishListDto;
import com.example.backend.admin.model.ViewRequestDto;
import com.example.backend.admin.service.StatisticsService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/statistics")
public class StatisticController {
    private final StatisticsService statisticsService;

    @GetMapping
    public BaseResponse<StatisticsResponseDto> statistics() {
        return new BaseResponseServiceImpl().getSuccessResponse(statisticsService.getStatistics(), CommonResponseStatus.SUCCESS);
    }

    // ---- 이 아래는 테스트용 api로 규칙을 따르지 않음----
    @GetMapping("/wishlist")
    public ResponseEntity<List<TopWishListDto>> getTopWishList() {
        return ResponseEntity.ok(statisticsService.getTopWishList());
    }

    @GetMapping("/refund")
    public ResponseEntity<Integer> getTotalRefund() {
        return ResponseEntity.ok(statisticsService.getTotalRefunds());
    }

    @GetMapping("/order")
    public ResponseEntity<Integer> getTotalOrder() {
        return ResponseEntity.ok(statisticsService.getTotalOrders());
    }

    @GetMapping("/topsales")
    public ResponseEntity<List<TopSales>> getTopSales() {
        return ResponseEntity.ok(statisticsService.getTopSales());
    }

    @PostMapping("/view")
    public void increaseView(@RequestBody ViewRequestDto request) {
        // TODO: product 테이블에 view 필드 추가되면 구현
    }
}
