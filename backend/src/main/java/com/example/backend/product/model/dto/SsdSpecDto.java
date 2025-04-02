package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.SsdSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SsdSpecDto {
    private Integer ssdCapacity;
    private Integer ssdRead;
    private Integer ssdWrite;

    public static SsdSpecDto from(SsdSpec spec) {
        return SsdSpecDto.builder()
                .ssdCapacity(spec.getSsdCapacity())
                .ssdRead(spec.getSsdRead())
                .ssdWrite(spec.getSsdWrite())
                .build();
    }
}
