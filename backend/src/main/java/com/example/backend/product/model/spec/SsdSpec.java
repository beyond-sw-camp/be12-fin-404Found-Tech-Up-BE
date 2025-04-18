package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.SsdSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SsdSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ssdIdx;
    private Integer ssdCapacity;
    private Integer ssdRead;
    private Integer ssdWrite;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(SsdSpecDto ssdSpecDto) {
        this.ssdCapacity = ssdSpecDto.getSsdCapacity();
        this.ssdRead = ssdSpecDto.getSsdRead();
        this.ssdWrite = ssdSpecDto.getSsdWrite();
    }
}
