package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductRequestDto {
    private String name;
    private Double price;
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
        return Product.builder()
                .name(name)
                .price(price)
                .brand(brand)
                .stock(stock)
                .description(description)
                .category(category)
                .build();
    }
}
