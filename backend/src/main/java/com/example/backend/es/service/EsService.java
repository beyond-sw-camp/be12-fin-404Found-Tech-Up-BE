package com.example.backend.es.service;

import com.example.backend.es.model.EsEntity;
import com.example.backend.es.repository.EsRepository;
import com.example.backend.es.repository.EsRepositoryImpl;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsService {
    private final EsRepository esRepository;
    private final ProductRepository productRepository;

    public List<ProductResponseDto> getItemBasedRecommendations(Long productIdx, Integer resultNum) {
        // 요청 전 시간 기록
        long startTime = System.currentTimeMillis();
        log.trace("Sending item-based recommendation request for productIdx={}", productIdx);
        EsEntity esEntity = esRepository.findEntityById(productIdx.toString(), "item").orElseThrow();
        List<EsEntity> results = esRepository.findSimilarEntities(esEntity.getVector(), resultNum+1, "item");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.trace("Received item-based recommendation response for productIdx={}, duration={} ms", productIdx, duration);
        return results.stream()
                .map(entity -> {
                    Long itemIdx = Long.parseLong(entity.getIdx()); // EsEntity의 idx를 Long으로 변환
                    return productRepository
                            .findById(itemIdx)
                            .map(ProductResponseDto::from)
                            .orElseThrow(() ->
                                    new RuntimeException("Product " + itemIdx + " not found")
                            );
                })
                .collect(Collectors.toList());
    }

    public List<EsEntity> getUserBasedRecommendations(Long userIdx, Integer resultNum) {
        long startTime = System.currentTimeMillis();
        log.trace("Sending item-based recommendation request for userIdx={}", userIdx);
        EsEntity esEntity = esRepository.findEntityById(userIdx.toString(), "user").orElseThrow();
        List<EsEntity> results = esRepository.findSimilarEntities(esEntity.getVector(), resultNum+1, "user");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.trace("Received item-based recommendation response for userIdx={}, duration={} ms", userIdx, duration);
        return results;
    }
}
