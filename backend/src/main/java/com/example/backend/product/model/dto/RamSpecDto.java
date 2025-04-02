package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.RamSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RamSpecDto {
    private String ramType;
    private Integer ramNum;
    private Integer ramSize;
    private String ramUsage;

    public static RamSpecDto from(RamSpec spec) {
        return RamSpecDto.builder()
                .ramType(spec.getRamType())
                .ramNum(spec.getRamNum())
                .ramSize(spec.getRamSize())
                .ramUsage(spec.getRamUsage())
                .build();
    }
}
