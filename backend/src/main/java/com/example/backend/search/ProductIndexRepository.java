package com.example.backend.search;

import com.example.backend.search.model.ProductIndexDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndexDocument, Long> {
    Page<ProductIndexDocument> findByProductName(String productName, Pageable pageable);
    Page<ProductIndexDocument> findByCategory(String category, Pageable pageable);
    Page<ProductIndexDocument> findByPriceBetween(Double lower, Double higher, Pageable pageable);
    Page<ProductIndexDocument> findByBrand(String brand, Pageable pageable);
    Page<ProductIndexDocument> findByDiscountGreaterThan(Integer discount, Pageable pageable);
    Page<ProductIndexDocument> findByStockGreaterThan(Integer stock, Pageable pageable);
}
