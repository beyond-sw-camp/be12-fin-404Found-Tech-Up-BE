package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.GpuSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GpuSpecDto {
    private String gpuChip;
    private Integer gpuMemory;
    private Integer gpuLength;

    public static GpuSpecDto from(GpuSpec spec) {
        return GpuSpecDto.builder()
                .gpuChip(spec.getGpuChip())
                .gpuMemory(spec.getGpuMemory())
                .gpuLength(spec.getGpuLength())
                .build();
    }
}
