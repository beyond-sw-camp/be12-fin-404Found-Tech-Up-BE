package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.CpuSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cpuIdx;
    private String cpuType;
    private Integer cpuCore;
    private Integer cpuThreads;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(CpuSpecDto cpuSpecDto) {
        this.cpuType = cpuSpecDto.getCpuType();
        this.cpuCore = cpuSpecDto.getCpuCore();
        this.cpuThreads = cpuSpecDto.getCpuThreads();
    }
}
