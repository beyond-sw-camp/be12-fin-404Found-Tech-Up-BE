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
public class GpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpuIdx;
    private String gpuChip;
    private Integer gpuMemory;
    private Integer gpuLength;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product productIdx;
}
