package com.example.backend.admin.model;

import com.example.backend.product.model.dto.ProductResponseDto;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StatisticsResponseDto {
    Integer totalOrders; //
    Double totalSales; //
    Integer totalRefunds; //
    List<ProductResponseDto> topViews; // TODO: 조회수 기록 필요
    List<TopSales> topSales; //
    List<TopWishListDto> topWishList; //
    List<String> topKeywords; // TODO: 구현 타이밍 모호함
    Integer newCustomers; //
}
