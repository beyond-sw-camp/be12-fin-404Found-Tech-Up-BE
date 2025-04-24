package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.CpuSpec;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CpuSpecDto {
    private String amdCpuType;
    private String socketType;
    private String coreCount;
    private String threadCount;
    private String memorySpec;
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
    private String memoryClock;
    private Integer memoryChannel;
    private String pakageType;
    private String cooler;
    private String registYear;
    private String registMonth;

    public static CpuSpecDto from(CpuSpec spec) {
        return CpuSpecDto.builder()
                .amdCpuType(spec.getAmdCpuType())
                .socketType(spec.getSocketType())
                .coreCount(spec.getCoreCount())
                .threadCount(spec.getThreadCount())
                .memorySpec(spec.getMemorySpec())
                .builtInGraphic(spec.getBuiltInGraphic())
                .manufactoringProcess(spec.getManufactoringProcess())
                .baseClock(spec.getBaseClock())
                .maxClock(spec.getMaxClock())
                .l2Cache(spec.getL2Cache())
                .l3Cache(spec.getL3Cache())
                .operateSystem(spec.getOperateSystem())
                .tdp(spec.getTdp())
                .ppt(spec.getPpt())
                .pcieVer(spec.getPcieVer())
                .maxPcie(spec.getMaxPcie())
                .maxMemorySize(spec.getMaxMemorySize())
                .memoryClock(spec.getMemoryClock())
                .memoryChannel(spec.getMemoryChannel())
                .pakageType(spec.getPakageType())
                .cooler(spec.getCooler())
                .registYear(spec.getRegistYear())
                .registMonth(spec.getRegistMonth())
                .build();
    }
}
