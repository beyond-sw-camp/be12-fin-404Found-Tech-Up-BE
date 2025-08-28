package com.example.backend.search;

import com.example.backend.product.model.dto.ReducedProductResponseDto;
import com.example.backend.product.repository.ProductRepository;

import com.example.backend.search.model.ProductIndexDocument;
import com.example.backend.util.HttpClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductIndexRepository productIndexRepository;

    @Value("${elasticsearch.host}")
    private String elasticHost;

    public List<ReducedProductResponseDto> searchByName(String name, String category, Integer page, Integer size) {
        try {
            List<ProductIndexDocument> nodes = HttpClientUtil.getSearchResults(elasticHost, category, name, page, size);
            return nodes.stream().map(ReducedProductResponseDto::from).toList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ReducedProductResponseDto> searchByNameAndCategory(String name, String category) {
        return productIndexRepository.findAllByProductnameContainingIgnoreCaseAndCategory(name,category).stream().map(ReducedProductResponseDto::from).toList();
    }

    public Page<ReducedProductResponseDto> searchByName(String name, Pageable pageable) {
        return productIndexRepository.findAllByProductnameContainingIgnoreCase(name, pageable).map(ReducedProductResponseDto::from);
    }

    public List<ReducedProductResponseDto> searchByNameAndCategoryAndPriceRange(String name, String category, Double low, Double high) {
        return productIndexRepository.findAllByProductnameContainingIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(name,category, low,high).stream().map(ReducedProductResponseDto::from).toList();
    }

}
