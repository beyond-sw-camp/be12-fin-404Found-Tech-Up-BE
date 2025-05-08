package com.example.backend.search;

import com.example.backend.search.model.ProductIndexDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndexDocument, Long> {
    @Query("{}")
    Page<ProductIndexDocument> findByProductNameIgnoreCase(String productName, Pageable pageable);
    Page<ProductIndexDocument> findByProductNameIgnoreCaseAndCategory(String name, String category, Pageable pageable);
    Page<ProductIndexDocument> findByProductNameIgnoreCaseAndPriceBetween(String name, Double lower, Double higher, Pageable pageable);
    Page<ProductIndexDocument> findByProductNameIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(String name, String category, Double lower, Double higher, Pageable pageable);
    // TODO: 아래 분류도 사용할 것인가?
    Page<ProductIndexDocument> findByBrandIgnoreCase(String brand, Pageable pageable);
    Page<ProductIndexDocument> findByDiscountGreaterThan(Integer discount, Pageable pageable);
    Page<ProductIndexDocument> findByStockGreaterThan(Integer stock, Pageable pageable);
}
