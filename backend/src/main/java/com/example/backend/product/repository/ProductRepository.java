package com.example.backend.product.repository;

import com.example.backend.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    Page<Product> findAllByCategoryIgnoreCase(String category, Pageable pageable);
}
