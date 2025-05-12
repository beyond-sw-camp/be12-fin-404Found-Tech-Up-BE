package com.example.backend.product.repository;

import com.example.backend.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategoryContaining(String keyword, String category, Pageable pageable);
    Page<Product> findAllByCategoryIgnoreCase(String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterProductsWithoutCategory(Double minPrice, Double maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category LIKE 'CPU' AND p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterCPUProducts( Double minPrice, Double maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category LIKE 'GPU' AND p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterGPUProducts( Double minPrice, Double maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category LIKE 'RAM' AND p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterRAMProducts( Double minPrice, Double maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category LIKE 'SSD' AND p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterSSDProducts( Double minPrice, Double maxPrice, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category LIKE 'HDD' AND p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> filterHDDProducts( Double minPrice, Double maxPrice, Pageable pageable);
}
