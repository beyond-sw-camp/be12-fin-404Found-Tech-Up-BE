package com.example.backend.product.service;

import com.example.backend.common.s3.S3Service;
import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.notification.service.NotificationProducerService;
import com.example.backend.product.model.Product;
import com.example.backend.product.model.ProductImage;
import com.example.backend.product.model.dto.ProductDeleteResponseDto;
import com.example.backend.product.model.dto.ProductFilterRequestDto;
import com.example.backend.product.model.dto.ProductRequestDto;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.model.spec.*;
import com.example.backend.product.repository.*;
import com.example.backend.review.model.Review;
import com.example.backend.wishlist.repository.WishlistRepository;
import com.example.backend.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
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
    // TODO: 실수로 잘못 등록한 기기에 대해 내 기기 등록한 사용자/리뷰한 사용자가 있는 경우를 대비해 강제 삭제하기 위한 리포지토리
    // private final UserProductRepository userProductRepository;
    private final ReviewRepository reviewRepository;

    // 재입고 알림 발행을 위해
    private final WishlistRepository wishlistRepository;
    // 카프카 알림 발행
    private final NotificationProducerService notificationProducerService;
    // 제품 이미지 파일 삭제는 이곳에서 처리
    private final S3Service s3Service;
    private final ProductImageService productImageService;

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
    @Transactional
    public ProductDeleteResponseDto deleteProduct(Long productId) {
        // Note: 쿠폰이 발급되었거나, 구매 기록이 있으면 삭제 불가!
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        if (!product.getImages().isEmpty()) {
            List<String> productImages = product.getImages().stream().map(image -> productImageService.getFileKeyForDelete(image.getImageUrl())).toList();
            s3Service.deleteFiles(productImages);
            productImageRepository.deleteAll(product.getImages());
        }
        if (product.getCpuSpec() != null) cpuSpecRepository.delete(product.getCpuSpec());
        if (product.getGpuSpec() != null) gpuSpecRepository.delete(product.getGpuSpec());
        if (product.getRamSpec() != null) ramSpecRepository.delete(product.getRamSpec());
        if (product.getSsdSpec() != null) ssdSpecRepository.delete(product.getSsdSpec());
        if (product.getHddSpec() != null) hddSpecRepository.delete(product.getHddSpec());
        try {
            productRepository.delete(product);
        } catch (Exception e) {
            throw new ProductException(ProductResponseStatus.PRODUCT_DELETE_FAIL);
        }
        return ProductDeleteResponseDto.from(productId);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        // --- 알림 판단을 위한 이전 상태 ---
        int beforeStock = product.getStock();
        int afterStock = requestDto.getStock();

        int oldDiscount = product.getDiscount() != null ? product.getDiscount() : 0;
        int newDiscount = requestDto.getDiscount() != null ? requestDto.getDiscount() : 0;

        // --- 제품 정보 업데이트 ---
        product.update(requestDto);
        product = productRepository.save(product);

        // --- 재입고 알림 처리 ---
        if (beforeStock == 0 && afterStock > 0) {
            List<Long> userIdxList = wishlistRepository.findUserIdxByProductIdx(productId);
            for (Long userIdx : userIdxList) {
                notificationProducerService.sendRestockNotification(productId, product.getName(), userIdx);
            }
        }

        // --- 가격 인하 알림 처리 ---
        if (newDiscount > oldDiscount) {
            List<Long> userIdxList = wishlistRepository.findUserIdxByProductIdx(productId);
            for (Long userIdx : userIdxList) {
                notificationProducerService.sendPriceDropNotification(
                        product.getProductIdx(),
                        product.getName(),
                        newDiscount,
                        userIdx
                );
            }
        }

        // --- 사양 업데이트 ---
        if (requestDto.getCpuSpec() != null) {
            CpuSpec cpuSpec = product.getCpuSpec();
            if (cpuSpec == null) {
                cpuSpec = new CpuSpec();
                cpuSpec.setProduct(product);
            }
            cpuSpec.update(requestDto.getCpuSpec());
            cpuSpecRepository.save(cpuSpec);
        }

        if (requestDto.getGpuSpec() != null) {
            GpuSpec gpuSpec = product.getGpuSpec();
            if (gpuSpec != null) {
                gpuSpecRepository.delete(gpuSpec);
                gpuSpec = new GpuSpec();
                gpuSpec.setProduct(product);
            }
            gpuSpec.update(requestDto.getGpuSpec());
            gpuSpecRepository.save(gpuSpec);
        }

        if (requestDto.getRamSpec() != null) {
            RamSpec ramSpec = product.getRamSpec();
            if (ramSpec != null) {
                ramSpecRepository.delete(ramSpec);
                ramSpec = new RamSpec();
                ramSpec.setProduct(product);
            }
            ramSpec.update(requestDto.getRamSpec());
            ramSpecRepository.save(ramSpec);
        }

        if (requestDto.getSsdSpec() != null) {
            SsdSpec ssdSpec = product.getSsdSpec();
            if (ssdSpec != null) {
                ssdSpecRepository.delete(ssdSpec);
                ssdSpec = new SsdSpec();
                ssdSpec.setProduct(product);
            }
            ssdSpec.update(requestDto.getSsdSpec());
            ssdSpecRepository.save(ssdSpec);
        }

        if (requestDto.getHddSpec() != null) {
            HddSpec hddSpec = product.getHddSpec();
            if (hddSpec != null) {
                hddSpecRepository.delete(hddSpec);
                hddSpec = new HddSpec();
                hddSpec.setProduct(product);
            }
            hddSpec.update(requestDto.getHddSpec());
            hddSpecRepository.save(hddSpec);
        }



        return ProductResponseDto.from(product);
    }

    //@Scheduled(cron = "0 0 */2 * * *")
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void recomputeAllProductRatings() {
        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            List<Review> reviews = reviewRepository.findByProduct(p);
            if (reviews.isEmpty()) continue;

            // compute average
            IntSummaryStatistics stats = reviews.stream()
                    .mapToInt(Review::getReviewRating)
                    .summaryStatistics();

            double avg = stats.getAverage();
            // persist as a double with one decimal
            p.setRating(Math.round(avg * 100.0) / 100.0);
            productRepository.save(p);
        }
    }
}
