package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.RamSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RamSpecDto {
    private String ramType;
    private Integer ramNum;
    private String ramUsage;

    public static RamSpecDto from(RamSpec spec) {
        return RamSpecDto.builder()
                .ramType(spec.getRamType())
                .ramNum(spec.getRamNum())
                .ramUsage(spec.getRamUsage())
                .build();
    }
}
