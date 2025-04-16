package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.GpuSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpuIdx;
    private String gpuChip;
    private Integer gpuMemory;
    private Integer gpuLength;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(GpuSpecDto gpuSpecDto) {
        this.gpuChip = gpuSpecDto.getGpuChip();
        this.gpuMemory = gpuSpecDto.getGpuMemory();
        this.gpuLength = gpuSpecDto.getGpuLength();
    }
}
