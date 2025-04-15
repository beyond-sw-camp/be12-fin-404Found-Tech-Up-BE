package com.example.backend.product.model.dto;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductResponseDto {
    private Long idx;
    private String name;
    private double price;
    private Integer discount;
    private String brand;
    private Integer stock;
    private String description;
    private String category;

    private CpuSpecDto cpuSpec;
    private GpuSpecDto gpuSpec;
    private HddSpecDto hddSpec;
    private SsdSpecDto ssdSpec;
    private RamSpecDto ramSpec;

    private List<String> images;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .idx(product.getProductIdx())
                .name(product.getName())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .brand(product.getBrand())
                .stock(product.getStock())
                .description(product.getDescription())
                .category(product.getCategory())
                .cpuSpec(product.getCpuSpec() != null ? CpuSpecDto.from(product.getCpuSpec()) : null)
                .gpuSpec(product.getGpuSpec() != null ? GpuSpecDto.from(product.getGpuSpec()) : null)
                .hddSpec(product.getHddSpec() != null ? HddSpecDto.from(product.getHddSpec()) : null)
                .ssdSpec(product.getSsdSpec() != null ? SsdSpecDto.from(product.getSsdSpec()) : null)
                .ramSpec(product.getRamSpec() != null ? RamSpecDto.from(product.getRamSpec()) : null)
                .images(product.getImages() != null ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()): new ArrayList<String>())
                .build();
    }
}
