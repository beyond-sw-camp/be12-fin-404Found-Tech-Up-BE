package com.example.backend.search;

import com.example.backend.search.model.ProductIndexDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductIndexRepository extends ElasticsearchRepository<ProductIndexDocument, Long> {
    Page<ProductIndexDocument> findAllByProductnameContainingIgnoreCase(String productName, Pageable pageable);
    List<ProductIndexDocument> findAllByProductnameContainingIgnoreCaseAndCategory(String name, String category);
    Page<ProductIndexDocument> findAllByProductnameContainingIgnoreCaseAndPriceBetween(String name, Double lower, Double higher, Pageable pageable);
    List<ProductIndexDocument> findAllByProductnameContainingIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(String name, String category, Double lower, Double higher);
    // TODO: 아래 분류도 사용할 것인가?
    List<ProductIndexDocument> findAllByBrandIgnoreCase(String brand, Pageable pageable);
    List<ProductIndexDocument> findAllByDiscountGreaterThan(Integer discount, Pageable pageable);
    List<ProductIndexDocument> findAllByStockGreaterThan(Integer stock, Pageable pageable);
}
