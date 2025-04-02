package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RamSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ramIdx;
    private String ramType;
    private Integer ramNum;
    private Integer ramSize;
    private String ramUsage;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;
}
