package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductRequestDto {
    private String name;
    private Double price;
    private Integer discount;
    private String brand;
    private Integer stock;
    private String description;
    private String category;

    private CpuSpecDto cpuSpec;
    private GpuSpecDto gpuSpec;
    private RamSpecDto ramSpec;
    private HddSpecDto hddSpec;
    private SsdSpecDto ssdSpec;

    public Product toEntity() {
        // 부품 별 스펙은 제외한다
        return Product.builder()
                .name(name)
                .price(price)
                .discount(discount)
                .brand(brand)
                .stock(stock)
                .description(description)
                .category(category)
                .cpuSpec(null)
                .gpuSpec(null)
                .ramSpec(null)
                .hddSpec(null)
                .ssdSpec(null)
                .build();
    }
}
