package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.RamSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RamSpecDto {
    private String usageDevice;
    private String productCategory;
    private String memorySpec;
    private String ramSize;
    private String operatingClock;
    private String ramTiming;
    private String operatingVoltage;
    private String ramNum;
    private String heatsink;
    private String height;
    private String registYear;
    private String registMonth;

    public static RamSpecDto from(RamSpec spec) {
        return RamSpecDto.builder()
                .usageDevice(spec.getUsageDevice())
                .productCategory(spec.getProductCategory())
                .memorySpec(spec.getMemorySpec())
                .ramSize(spec.getRamSize())
                .operatingClock(spec.getOperatingClock())
                .ramTiming(spec.getRamTiming())
                .operatingVoltage(spec.getOperatingVoltage())
                .ramNum(spec.getRamNum())
                .heatsink(spec.getHeatsink())
                .height(spec.getHeight())
                .registYear(spec.getRegistYear())
                .registMonth(spec.getRegistMonth())
                .build();
    }
}
