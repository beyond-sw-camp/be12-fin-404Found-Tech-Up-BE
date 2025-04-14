package com.example.backend.product.model.dto;

import lombok.Getter;

@Getter
public class ProductFilterRequestDto {
    private String nameKeyword;
    private String brand;
    private String category;
    private Integer discount;
    private Double minPrice;
    private Double maxPrice;

    // 카테고리별 상세 조건
    // CPU 스펙 조건
    private String cpuType;
    private Integer cpuCore;
    private Integer cpuThreads;

    // RAM 스펙 조건
    private String ramType;
    private Integer ramSize;
    private Integer ramNum;
    private String ramUsage;

    // SSD 스펙 조건
    private Integer ssdCapacity;
    private Integer ssdRead;
    private Integer ssdWrite;

    // HDD 스펙 조건
    private Integer hddCapacity;
    private Integer hddRpm;
    private Integer hddBuffer;

    // GPU 스펙 조건
    private String gpuChip;
    private Integer gpuMemory;
    private Integer gpuLength;
}
