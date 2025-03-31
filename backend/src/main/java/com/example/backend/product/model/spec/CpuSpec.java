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
public class CpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cpuIdx;
    private String cpuType;
    private Integer cpuCore;
    private Integer cpuThreads;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product productIdx;
}
