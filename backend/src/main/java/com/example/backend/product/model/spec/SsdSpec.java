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
public class SsdSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ssdIdx;
    private Integer ssdCapacity;
    private Integer ssdRead;
    private Integer ssdWrite;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product productIdx;
}
