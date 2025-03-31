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
    private int idx;
    private int ssdCapacity;
    private int ssdRead;
    private int ssdWrite;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product productIdx;
}
