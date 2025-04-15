package com.example.backend.product.service;

import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.product.model.dto.ProductDeleteResponseDto;
import com.example.backend.product.model.dto.ProductFilterRequestDto;
import com.example.backend.product.model.dto.ProductRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.model.spec.*;
import com.example.backend.product.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    // 영속성 이슈로 불가피하게 스펙 리포지토리를 만듦(F Key가 스펙 테이블 쪽에 있기 때문)
    private final CpuSpecRepository cpuSpecRepository;
    private final GpuSpecRepository gpuSpecRepository;
    private final RamSpecRepository ramSpecRepository;
    private final SsdSpecRepository ssdSpecRepository;
    private final HddSpecRepository hddSpecRepository;
    private final ProductImageRepository productImageRepository;

    public List<ProductResponseDto> getProductList() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
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
                .filter(product -> dto.getDiscount() == null || product.getDiscount() >= dto.getDiscount())
                .filter(product -> dto.getMinPrice() == null || product.getPrice() >= dto.getMinPrice())
                .filter(product -> dto.getMaxPrice() == null || product.getPrice() <= dto.getMaxPrice())
                .filter(product -> dto.getNameKeyword() == null || product.getName().toLowerCase().contains(dto.getNameKeyword().toLowerCase()))
                .filter(product -> {
                    String category = product.getCategory() == null ? "" : product.getCategory().toUpperCase();

                    return switch (category) {
                        case "CPU" -> {
                            if (product.getCpuSpec() == null) yield false;
                            yield (dto.getCpuType() == null || product.getCpuSpec().getCpuType().equalsIgnoreCase(dto.getCpuType())) &&
                                    (dto.getCpuCore() == null || product.getCpuSpec().getCpuCore().equals(dto.getCpuCore())) &&
                                    (dto.getCpuThreads() == null || product.getCpuSpec().getCpuThreads().equals(dto.getCpuThreads()));
                        }
                        case "GPU" -> {
                            if (product.getGpuSpec() == null) yield false;
                            yield (dto.getGpuChip() == null || product.getGpuSpec().getGpuChip().equalsIgnoreCase(dto.getGpuChip())) &&
                                    (dto.getGpuMemory() == null || product.getGpuSpec().getGpuMemory().equals(dto.getGpuMemory())) &&
                                    (dto.getGpuLength() == null || product.getGpuSpec().getGpuLength().equals(dto.getGpuLength()));
                        }
                        case "RAM" -> {
                            if (product.getRamSpec() == null) yield false;
                            yield (dto.getRamType() == null || product.getRamSpec().getRamType().equalsIgnoreCase(dto.getRamType())) &&
                                    (dto.getRamSize() == null || product.getRamSpec().getRamSize().equals(dto.getRamSize())) &&
                                    (dto.getRamNum() == null || product.getRamSpec().getRamNum().equals(dto.getRamNum())) &&
                                    (dto.getRamUsage() == null || product.getRamSpec().getRamUsage().equalsIgnoreCase(dto.getRamUsage()));
                        }
                        case "SSD" -> {
                            if (product.getSsdSpec() == null) yield false;
                            yield (dto.getSsdCapacity() == null || product.getSsdSpec().getSsdCapacity().equals(dto.getSsdCapacity())) &&
                                    (dto.getSsdRead() == null || product.getSsdSpec().getSsdRead().equals(dto.getSsdRead())) &&
                                    (dto.getSsdWrite() == null || product.getSsdSpec().getSsdWrite().equals(dto.getSsdWrite()));
                        }
                        case "HDD" -> {
                            if (product.getHddSpec() == null) yield false;
                            yield (dto.getHddCapacity() == null || product.getHddSpec().getHddCapacity().equals(dto.getHddCapacity())) &&
                                    (dto.getHddRpm() == null || product.getHddSpec().getHddRpm().equals(dto.getHddRpm())) &&
                                    (dto.getHddBuffer() == null || product.getHddSpec().getHddBuffer().equals(dto.getHddBuffer()));
                        }
                        default -> true; // 그 외 카테고리 처리
                    };
                })
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto registerProduct(ProductRequestDto requestDto) {
        Product source = requestDto.toEntity();
        Product savedProduct = productRepository.save(source);
        if (requestDto.getCategory().equals("CPU")){
            CpuSpec cpuSpec = CpuSpec.builder().cpuType(requestDto.getCpuSpec().getCpuType()).cpuCore(requestDto.getCpuSpec().getCpuCore()).cpuThreads(requestDto.getCpuSpec().getCpuThreads()).product(savedProduct).build();
            cpuSpecRepository.save(cpuSpec);
        } else if (requestDto.getCategory().equals("GPU")){
            GpuSpec gpuSpec = GpuSpec.builder().gpuChip(requestDto.getGpuSpec().getGpuChip()).gpuMemory(requestDto.getGpuSpec().getGpuMemory()).gpuLength(requestDto.getGpuSpec().getGpuLength()).product(savedProduct).build();
            gpuSpecRepository.save(gpuSpec);
        } else if (requestDto.getCategory().equals("RAM")){
            RamSpec ramSpec = RamSpec.builder().ramType(requestDto.getRamSpec().getRamType()).ramNum(requestDto.getRamSpec().getRamNum()).ramSize(requestDto.getRamSpec().getRamSize()).ramUsage(requestDto.getRamSpec().getRamUsage()).product(savedProduct).build();
            ramSpecRepository.save(ramSpec);
        } else if (requestDto.getCategory().equals("SSD")){
            SsdSpec ssdSpec = SsdSpec.builder().ssdCapacity(requestDto.getSsdSpec().getSsdCapacity()).ssdRead(requestDto.getSsdSpec().getSsdRead()).ssdWrite(requestDto.getSsdSpec().getSsdWrite()).product(savedProduct).build();
            ssdSpecRepository.save(ssdSpec);
        } else if (requestDto.getCategory().equals("HDD")){
            HddSpec hddSpec = HddSpec.builder().hddCapacity(requestDto.getHddSpec().getHddCapacity()).hddRpm(requestDto.getHddSpec().getHddRpm()).hddBuffer(requestDto.getHddSpec().getHddBuffer()).product(savedProduct).build();
            hddSpecRepository.save(hddSpec);
        }

        return ProductResponseDto.from(savedProduct);
    }

    public ProductDeleteResponseDto deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
        return ProductDeleteResponseDto.from(productId);
    }

    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        product.update(requestDto);
        return ProductResponseDto.from(product);
    }

}
