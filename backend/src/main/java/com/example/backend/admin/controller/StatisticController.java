package com.example.backend.admin.controller;

import com.example.backend.admin.model.StatisticsResponseDto;
import com.example.backend.admin.model.TopSalesDto;
import com.example.backend.admin.model.TopWishListDto;
import com.example.backend.admin.model.ViewRequestDto;
import com.example.backend.admin.service.StatisticsService;
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
    public ResponseEntity<StatisticsResponseDto> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

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
    public ResponseEntity<List<TopSalesDto>> getTopSales() {
        return ResponseEntity.ok(statisticsService.getTopSales());
    }

    @PostMapping("/view")
    public void increaseView(@RequestBody ViewRequestDto request) {
        // TODO: product 테이블에 view 필드 추가되면 구현
    }
}
