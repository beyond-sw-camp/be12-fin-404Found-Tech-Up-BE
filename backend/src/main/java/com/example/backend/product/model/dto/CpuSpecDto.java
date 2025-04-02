package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.CpuSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CpuSpecDto {
    private String cpuType;
    private Integer cpuCore;
    private Integer cpuThreads;

    public static CpuSpecDto from(CpuSpec spec) {
        return CpuSpecDto.builder()
                .cpuType(spec.getCpuType())
                .cpuCore(spec.getCpuCore())
                .cpuThreads(spec.getCpuThreads())
                .build();
    }
}
