package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.RamSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    public void update(RamSpecDto ramSpecDto) {
        this.ramType=ramSpecDto.getRamType();
        this.ramNum=ramSpecDto.getRamNum();
        this.ramSize=ramSpecDto.getRamSize();
        this.ramUsage=ramSpecDto.getRamUsage();
    }
}
