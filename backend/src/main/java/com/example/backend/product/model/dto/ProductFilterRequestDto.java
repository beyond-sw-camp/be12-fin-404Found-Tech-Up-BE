package com.example.backend.product.model.dto;

import jakarta.persistence.Column;
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
    private String amdCpuType;
    private String socketType;
    private String coreCount;
    private String threadCount;
    private String cpuMemorySpec;
    private String builtInGraphic;
    private String manufactoringProcess;
    private String baseClock;
    private String maxClock;
    private String l2Cache;
    private String l3Cache;
    private String operateSystem;
    private String tdp;
    private String ppt;
    private String pcieVer;
    private String maxPcie;
    private String maxMemorySize;
    private String cpuMemoryClock;
    private Integer memoryChannel;
    private String pakageType;
    private String cooler;
    private String cpuRegistYear;
    private String cpuRegistMonth;

    // GPU 스펙 조건
    private String chipsetManufacturer;
    private String productSeries;
    private String gpuManufacturingProcess;
    private String gpuInterfaceType;
    private String recommendedPowerCapacity;
    private String powerPort;
    private String gpuLength;
    private String boostClock;
    private String streamProcessor;
    private String gpuMemoryType;
    private String memoryClock;
    private String gpuMemory;
    private String memoryBus;
    private String hdmi;
    private String displayPort;
    private String monitorSupport;
    private String fanCount;
    private String gpuThickness;
    private String gpuRegistYear;
    private String gpuRegistMonth;

    // RAM 스펙 조건
    private String usageDevice;
    private String ramProductCategory;
    private String memorySpec;
    private String ramSize;
    private String operatingClock;
    private String ramTiming;
    private String operatingVoltage;
    private String ramNum;
    private String heatsink;
    private String ramHeight;
    private String ramRegistYear;
    private String ramRegistMonth;

    // SSD 스펙 조건
    private String ssdProductCategory;
    private String formFactor;
    private String ssdInterfaceType;
    private String protocol;
    private String ssdCapacity;
    private String memoryType;
    private String nandStructure;
    private String controller;
    private String ssdRead;
    private String ssdWrite;
    private String mtbf;
    private String tbw;
    private String nvmeHeatsink;
    private String width;
    private String height;
    private String ssdThickness;
    private String weight;
    private String ssdRegistYear;
    private String ssdRegistMonth;

    // HDD 스펙 조건
    private String hddProductCategory;
    private String diskSize;
    private String hddCapacity;
    private String hddInterfaceType;
    private String hddRpm;
    private String hddBuffer;
    private String transferSpeed;
    private String recordingMethod;
    private String hddThickness;
    private String workload;
    private String noise;
    private String hddRegistYear;
    private String hddRegistMonth;

}
