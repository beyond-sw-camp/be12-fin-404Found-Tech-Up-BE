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

    // 리뷰와 일대다 맵핑

    // 사용자의 제품과 일대다 맵핑

    // 쿠폰과 일대다 맵핑

    // 장바구니와 일대다 맵핑

    // 주문 상세 정보와 일대다 맵핑

    // 위시리스트와 일대다 맵핑
}
