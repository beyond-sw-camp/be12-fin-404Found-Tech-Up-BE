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
    List<ProductResponseDto> topViews; // TODO
    List<ProductResponseDto> topSales; // TODO
    List<ProductResponseDto> topWishList; // TODO
    List<String> topKeywords; // TODO
    Integer newCustomers; //
}
