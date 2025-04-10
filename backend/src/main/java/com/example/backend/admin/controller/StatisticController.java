package com.example.backend.admin.controller;

import com.example.backend.admin.model.StatisticsResponseDto;
import com.example.backend.admin.model.ViewRequestDto;
import com.example.backend.admin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/statistics")
public class StatisticController {
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponseDto> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @PostMapping("/view")
    public void increaseView(@RequestBody ViewRequestDto request) {
        // TODO: product 테이블에 view 필드 추가되면 구현
    }
}
