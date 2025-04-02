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
}
