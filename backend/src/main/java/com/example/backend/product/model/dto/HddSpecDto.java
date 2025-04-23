package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.HddSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HddSpecDto {
    private String productCategory;
    private String diskSize;
    private String hddCapacity;
    private String interfaceType;
    private String hddRpm;
    private String hddBuffer;
    private String transferSpeed;
    private String recordingMethod;
    private String thickness;
    private String workload;
    private String noise;
    private String registYear;
    private String registMonth;

    public static HddSpecDto from(HddSpec spec) {
        return HddSpecDto.builder()
                .productCategory(spec.getProductCategory())
                .diskSize(spec.getDiskSize())
                .hddCapacity(spec.getHddCapacity())
                .interfaceType(spec.getInterfaceType())
                .hddRpm(spec.getHddRpm())
                .hddBuffer(spec.getHddBuffer())
                .transferSpeed(spec.getTransferSpeed())
                .recordingMethod(spec.getRecordingMethod())
                .thickness(spec.getThickness())
                .workload(spec.getWorkload())
                .noise(spec.getNoise())
                .registYear(spec.getRegistYear())
                .registMonth(spec.getRegistMonth())
                .build();
    }
}
