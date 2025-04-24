package com.example.backend.product.model.dto;

import com.example.backend.product.model.spec.SsdSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SsdSpecDto {
    private String productCategory;
    private String formFactor;
    private String interfaceType;
    private String protocol;
    private String ssdCapacity;
    private String memoryType;
    private String nandStructure;
    private String controller;
    private String ssdRead;
    private String ssdWrite;
    private String mtbf;
    private String tbw;
    private String nvmeHeatsink;
    private String width;
    private String height;
    private String thickness;
    private String weight;
    private String registYear;
    private String registMonth;

    public static SsdSpecDto from(SsdSpec spec) {
        return SsdSpecDto.builder()
                .productCategory(spec.getProductCategory())
                .formFactor(spec.getFormFactor())
                .interfaceType(spec.getInterfaceType())
                .protocol(spec.getProtocol())
                .ssdCapacity(spec.getSsdCapacity())
                .memoryType(spec.getMemoryType())
                .nandStructure(spec.getNandStructure())
                .controller(spec.getController())
                .ssdRead(spec.getSsdRead())
                .ssdWrite(spec.getSsdWrite())
                .mtbf(spec.getMtbf())
                .tbw(spec.getTbw())
                .nvmeHeatsink(spec.getNvmeHeatsink())
                .width(spec.getWidth())
                .height(spec.getHeight())
                .thickness(spec.getThickness())
                .weight(spec.getWeight())
                .registYear(spec.getRegistYear())
                .registMonth(spec.getRegistMonth())
                .build();
    }
}
