package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.HddSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HddSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hddIdx;
    private Integer hddCapacity;
    private Integer hddRpm;
    private Integer hddBuffer;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(HddSpecDto hddSpecDto) {
        this.hddCapacity = hddSpecDto.getHddCapacity();
        this.hddRpm = hddSpecDto.getHddRpm();
        this.hddBuffer = hddSpecDto.getHddBuffer();
    }
}
