package com.example.backend.search;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.ReducedProductResponseDto;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.search.model.ProductIndexDocument;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductIndexRepository productIndexRepository;
    private final ProductRepository productRepository;

    public Page<ReducedProductResponseDto> searchByName(String name, Pageable pageable) {
        return productIndexRepository.findByProductNameIgnoreCase(name,pageable).map(ReducedProductResponseDto::from);
    }

    public Page<ReducedProductResponseDto> searchByNameAndCategory(String name, String category, Pageable pageable) {
        return productIndexRepository.findByProductNameIgnoreCaseAndCategory(name,category,pageable).map(ReducedProductResponseDto::from);
    }

    public Page<ReducedProductResponseDto> searchByNameAndPriceRange(String name, Double low, Double high, Pageable pageable) {
        return productIndexRepository.findByProductNameIgnoreCaseAndPriceBetween(name,low,high,pageable).map(ReducedProductResponseDto::from);
    }

    public Page<ReducedProductResponseDto> searchByNameAndCategoryAndPriceRange(String name, String category, Double low, Double high, Pageable pageable) {
        return productIndexRepository.findByProductNameIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(name,category, low,high,pageable).map(ReducedProductResponseDto::from);
    }

    @Transactional
    public void createIndex() {
        // 이전 색인 존재 여부 확인
        Iterable<ProductIndexDocument> previous = productIndexRepository.findAll();
        if (previous.iterator().hasNext()) {
            productIndexRepository.deleteAll();
        }
        List<ProductIndexDocument> index = productRepository.findAll().stream().map(product->product.toSearchDocument()).toList();
        productIndexRepository.saveAll(index);
    }
}
