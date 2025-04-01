package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.HddSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HddSpecDto {
    private Integer hddCapacity;
    private Integer hddRpm;
    private Integer hddBuffer;

    public static HddSpecDto from(HddSpec spec) {
        return HddSpecDto.builder()
                .hddCapacity(spec.getHddCapacity())
                .hddRpm(spec.getHddRpm())
                .hddBuffer(spec.getHddBuffer())
                .build();
    }
}
