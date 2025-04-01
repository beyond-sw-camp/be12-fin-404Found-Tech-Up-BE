package com.example.backend.product.service;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.ProductFilterRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto> getProductList() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductResponseDto.from(product);
    }

    public List<ProductResponseDto> searchProduct(String keyword) {
        return productRepository.findByNameContaining(keyword)
                .stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> filterProduct(ProductFilterRequestDto dto) {
        return productRepository.findAll().stream()
                .filter(product -> dto.getBrand() == null || product.getBrand().equalsIgnoreCase(dto.getBrand()))
                .filter(product -> dto.getCategory() == null || product.getCategory().equalsIgnoreCase(dto.getCategory()))
                .filter(product -> dto.getMinPrice() == null || product.getPrice() >= dto.getMinPrice())
                .filter(product -> dto.getMaxPrice() == null || product.getPrice() <= dto.getMaxPrice())
                .filter(product -> dto.getNameKeyword() == null || product.getName().toLowerCase().contains(dto.getNameKeyword().toLowerCase()))
                .filter(product -> {
                    if (product.getCategory() == null) return true;
                    switch (product.getCategory().toUpperCase()) {
                        case "CPU" -> {
                            return dto.getCpuCore() == null || (
                                    product.getCpuSpec() != null &&
                                            product.getCpuSpec().getCpuCore().equals(dto.getCpuCore())
                            );
                        }
                        case "RAM" -> {
                            return dto.getRamSize() == null || (
                                    product.getRamSpec() != null &&
                                            product.getRamSpec().getRamSize().equals(dto.getRamSize())
                            );
                        }
                        case "SSD" -> {
                            return dto.getSsdCapacity() == null || (
                                    product.getSsdSpec() != null &&
                                            product.getSsdSpec().getSsdCapacity().equals(dto.getSsdCapacity())
                            );
                        }
                        case "HDD" -> {
                            return dto.getHddRpm() == null || (
                                    product.getHddSpec() != null &&
                                            product.getHddSpec().getHddRpm().equals(dto.getHddRpm())
                            );
                        }
                        default -> {
                            return true;
                        }
                    }
                })
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

}
