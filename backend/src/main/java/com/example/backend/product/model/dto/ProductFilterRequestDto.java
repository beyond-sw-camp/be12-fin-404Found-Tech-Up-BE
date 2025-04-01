package com.example.backend.product.model.dto;

import lombok.Getter;

@Getter
public class ProductFilterRequestDto {
    private String brand;
    private String category;
    private Double minPrice;
    private Double maxPrice;

    // 카테고리별 상세 조건
    private Integer cpuCore;
    private Integer ramSize;
    private Integer ssdCapacity;
    private Integer hddRpm;
}
