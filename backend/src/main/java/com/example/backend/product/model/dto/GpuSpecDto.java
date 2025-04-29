package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.GpuSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GpuSpecDto {
    private String chipsetManufacturer;
    private String productSeries;
    private String gpuManufacturingProcess;
    private String interfaceType;
    private String recommendedPowerCapacity;
    private String powerPort;
    private String gpuLength;
    private String boostClock;
    private String streamProcessor;
    private String memoryType;
    private String memoryClock;
    private String gpuMemory;
    private String memoryBus;
    private String hdmi;
    private String displayPort;
    private String monitorSupport;
    private String fanCount;
    private String thickness;
    private String registYear;
    private String registMonth;

    public static GpuSpecDto from(GpuSpec spec) {
        return GpuSpecDto.builder()
                .chipsetManufacturer(spec.getChipsetManufacturer())
                .productSeries(spec.getProductSeries())
                .gpuManufacturingProcess(spec.getGpuManufacturingProcess())
                .interfaceType(spec.getInterfaceType())
                .recommendedPowerCapacity(spec.getRecommendedPowerCapacity())
                .powerPort(spec.getPowerPort())
                .gpuLength(spec.getGpuLength())
                .boostClock(spec.getBoostClock())
                .streamProcessor(spec.getStreamProcessor())
                .memoryType(spec.getMemoryType())
                .memoryClock(spec.getMemoryClock())
                .gpuMemory(spec.getGpuMemory())
                .memoryBus(spec.getMemoryBus())
                .hdmi(spec.getHdmi())
                .displayPort(spec.getDisplayPort())
                .monitorSupport(spec.getMonitorSupport())
                .fanCount(spec.getFanCount())
                .thickness(spec.getThickness())
                .registYear(spec.getRegistYear())
                .registMonth(spec.getRegistMonth())
                .build();
    }
}
