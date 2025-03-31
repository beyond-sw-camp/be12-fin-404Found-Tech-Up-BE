package com.example.backend.product.model;

import com.example.backend.product.model.spec.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;
    private String name;
    private double price;
    private String brand;
    private int stock;
    private String description;
    private String category;

    @OneToMany(mappedBy = "product_image")
    private List<ProductImage> images;

    @OneToOne(mappedBy = "product")
    private CpuSpec cpuSpec;

    @OneToOne(mappedBy = "product")
    private GpuSpec gpuSpec;

    @OneToOne(mappedBy = "product")
    private RamSpec ramSpec;

    @OneToOne(mappedBy = "product")
    private SsdSpec ssdSpec;

    @OneToOne(mappedBy = "product")
    private HddSpec hddSpec;
}
