package com.example.backend.search;

import com.example.backend.search.model.ProductIndexDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndexDocument, Long> {
    List<ProductIndexDocument> findByProductnameContainingIgnoreCase(String productName);
    List<ProductIndexDocument> findByProductnameContainingIgnoreCaseAndCategory(String name, String category);
    List<ProductIndexDocument> findByProductnameContainingIgnoreCaseAndPriceBetween(String name, Double lower, Double higher);
    List<ProductIndexDocument> findByProductnameContainingIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(String name, String category, Double lower, Double higher);
    // TODO: 아래 분류도 사용할 것인가?
    List<ProductIndexDocument> findByBrandIgnoreCase(String brand, Pageable pageable);
    List<ProductIndexDocument> findByDiscountGreaterThan(Integer discount, Pageable pageable);
    List<ProductIndexDocument> findByStockGreaterThan(Integer stock, Pageable pageable);
}
