package com.example.backend.product.service;

import lombok.extern.slf4j.Slf4j;
import com.example.backend.common.s3.S3Service;
import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.notification.service.NotificationProducerService;
import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.*;
import com.example.backend.product.model.spec.*;
import com.example.backend.product.repository.*;
import com.example.backend.review.model.Review;
import com.example.backend.wishlist.repository.WishlistRepository;
import com.example.backend.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    private final RestTemplate restTemplate = new RestTemplate();

    public Page<ProductResponseDto> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDto::from);
    }

    public Page<ProductResponseDto> getProductList(String category, Pageable pageable) {
        return productRepository.findAllByCategoryIgnoreCase(category, pageable)
                .map(ProductResponseDto::from);
    }
    /*
    public Page<ProductResponseDto> getProductListWithSpec(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDto::from);
    }

    public Page<ProductResponseDto> getProductListWithSpec(String category, Pageable pageable) {
        return productRepository.findAllByCategoryIgnoreCase(category, pageable)
                .map(ProductResponseDto::from);
    }
    */
    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        return ProductResponseDto.from(product);
    }

    public Page<ProductResponseDto> searchProduct(String keyword, String category, Pageable pageable) {
        if (category == null || category.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                    .map(ProductResponseDto::from);
        }
        return productRepository.findByNameContainingIgnoreCaseAndCategoryContaining(keyword, category, pageable)
                .map(ProductResponseDto::from);
    }

    public Page<ProductResponseDto> filterProduct(ProductFilterRequestDto dto, Pageable pageable) {
        if (dto.getCategory() == null || dto.getCategory().isEmpty()) {
            return productRepository.filterProductsWithoutCategory( dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
        }
        else {
            return switch (dto.getCategory().toUpperCase()) {
                case "CPU" ->
                        productRepository.filterCPUProducts(dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
                case "GPU" ->
                        productRepository.filterGPUProducts(dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
                case "RAM" ->
                        productRepository.filterRAMProducts(dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
                case "SSD" ->
                        productRepository.filterSSDProducts(dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
                case "HDD" ->
                        productRepository.filterHDDProducts(dto.getMinPrice(), dto.getMaxPrice(), pageable).map(ProductResponseDto::from);
                default -> throw new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND);
            };
        }
    }

    public List<ProductResponseDto> getContentBasedRecommendations(Long productIdx, Integer resultNum) {
        String url = "http://recommender-svc:8000/recommend";

        // 요청 전 시간 기록
        long startTime = System.currentTimeMillis();
        log.trace("Sending item-based recommendation request for productIdx={} to URL={}", productIdx, url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> payload = new HashMap<>();
        payload.put("product_idx", productIdx);
        payload.put("result_num", resultNum);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<ContentRecommendResponseDto> resp;
        try{
            resp = restTemplate.postForEntity(url, request, ContentRecommendResponseDto.class);

        } finally {
            // 요청 후 시간 기록 및 소요 시간 계산
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.trace("Received item-based recommendation response for productIdx={}, duration={} ms", productIdx, duration);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException(
                    "Failed to fetch recommendations (status="
                            + resp.getStatusCode() + ")"
            );
        }

        return resp.getBody()
                .getSimilarProducts()
                .stream()
                .map(rec -> productRepository
                        .findById(rec.getProductIdx())
                        .map(ProductResponseDto::from)
                        .orElseThrow(() ->
                                new RuntimeException("Product " + rec.getProductIdx() + " not found")
                        )
                )
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getItemBasedRecommendations(Long productIdx, Integer resultNum) {
        String url = "http://recommender-svc:8000/recommend/item-based";

        // 요청 전 시간 기록
        long startTime = System.currentTimeMillis();
        log.trace("Sending item-based recommendation request for productIdx={} to URL={}", productIdx, url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> payload = new HashMap<>();
        payload.put("product_idx", productIdx);
        payload.put("result_num", resultNum);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<RecommendResponseDto> resp;
        try {
            resp = restTemplate.postForEntity(url, request, RecommendResponseDto.class);
        } finally {
            // 요청 후 시간 기록 및 소요 시간 계산
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.trace("Received item-based recommendation response for productIdx={}, duration={} ms", productIdx, duration);
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException(
                    "Failed to fetch recommendations (status="
                            + resp.getStatusCode() + ")"
            );
        }

        return resp.getBody()
                .getRecommendedProducts()
                .stream()
                .map(rec -> productRepository
                        .findById(rec.getProductIdx())
                        .map(ProductResponseDto::from)
                        .orElseThrow(() ->
                                new RuntimeException("Product " + rec.getProductIdx() + " not found")
                        )
                )
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getUserBasedRecommendations(Long userIdx, Integer resultNum) {
        String url = "http://recommender-svc:8000/recommend/user-based";

        // 요청 전 시간 기록
        long startTime = System.currentTimeMillis();
        log.trace("Sending item-based recommendation request for userIdx={} to URL={}", userIdx, url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> payload = new HashMap<>();
        payload.put("user_idx", userIdx);
        payload.put("result_num", resultNum);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<RecommendResponseDto> resp;

        try {
            resp = restTemplate.postForEntity(url, request, RecommendResponseDto.class);
        } finally {
            // 요청 후 시간 기록 및 소요 시간 계산
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.trace("Received item-based recommendation response for userIdx={}, duration={} ms", userIdx, duration);
        }


        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException(
                    "Failed to fetch recommendations (status="
                            + resp.getStatusCode() + ")"
            );
        }

        return resp.getBody()
                .getRecommendedProducts()
                .stream()
                .map(rec -> productRepository
                        .findById(rec.getProductIdx())
                        .map(ProductResponseDto::from)
                        .orElseThrow(() ->
                                new RuntimeException("Product " + rec.getProductIdx() + " not found")
                        )
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto registerProduct(ProductRequestDto requestDto) {
        Product saved = productRepository.save(requestDto.toEntity());

        String cat = requestDto.getCategory();
        switch(cat.toUpperCase()) {
            case "CPU": {
                var dto = requestDto.getCpuSpec();
                CpuSpec spec = CpuSpec.builder()
                        .amdCpuType(dto.getAmdCpuType())
                        .socketType(dto.getSocketType())
                        .coreCount(dto.getCoreCount())
                        .threadCount(dto.getThreadCount())
                        .memorySpec(dto.getMemorySpec())
                        .builtInGraphic(dto.getBuiltInGraphic())
                        .manufactoringProcess(dto.getManufactoringProcess())
                        .baseClock(dto.getBaseClock())
                        .maxClock(dto.getMaxClock())
                        .l2Cache(dto.getL2Cache())
                        .l3Cache(dto.getL3Cache())
                        .operateSystem(dto.getOperateSystem())
                        .tdp(dto.getTdp())
                        .ppt(dto.getPpt())
                        .pcieVer(dto.getPcieVer())
                        .maxPcie(dto.getMaxPcie())
                        .maxMemorySize(dto.getMaxMemorySize())
                        .memoryClock(dto.getMemoryClock())
                        .memoryChannel(dto.getMemoryChannel())
                        .pakageType(dto.getPakageType())
                        .cooler(dto.getCooler())
                        .registYear(dto.getRegistYear())
                        .registMonth(dto.getRegistMonth())
                        .product(saved)
                        .build();
                cpuSpecRepository.save(spec);
                break;
            }
            case "GPU": {
                var dto = requestDto.getGpuSpec();
                GpuSpec spec = GpuSpec.builder()
                        .chipsetManufacturer(dto.getChipsetManufacturer())
                        .productSeries(dto.getProductSeries())
                        .gpuManufacturingProcess(dto.getGpuManufacturingProcess())
                        .interfaceType(dto.getInterfaceType())
                        .recommendedPowerCapacity(dto.getRecommendedPowerCapacity())
                        .powerPort(dto.getPowerPort())
                        .gpuLength(dto.getGpuLength())
                        .boostClock(dto.getBoostClock())
                        .streamProcessor(dto.getStreamProcessor())
                        .memoryType(dto.getMemoryType())
                        .memoryClock(dto.getMemoryClock())
                        .gpuMemory(dto.getGpuMemory())
                        .memoryBus(dto.getMemoryBus())
                        .hdmi(dto.getHdmi())
                        .displayPort(dto.getDisplayPort())
                        .monitorSupport(dto.getMonitorSupport())
                        .fanCount(dto.getFanCount())
                        .thickness(dto.getThickness())
                        .registYear(dto.getRegistYear())
                        .registMonth(dto.getRegistMonth())
                        .product(saved)
                        .build();
                gpuSpecRepository.save(spec);
                break;
            }
            case "RAM": {
                var dto = requestDto.getRamSpec();
                RamSpec spec = RamSpec.builder()
                        .usageDevice(dto.getUsageDevice())
                        .productCategory(dto.getProductCategory())
                        .memorySpec(dto.getMemorySpec())
                        .ramSize(dto.getRamSize())
                        .operatingClock(dto.getOperatingClock())
                        .ramTiming(dto.getRamTiming())
                        .operatingVoltage(dto.getOperatingVoltage())
                        .ramNum(dto.getRamNum())
                        .heatsink(dto.getHeatsink())
                        .height(dto.getHeight())
                        .registYear(dto.getRegistYear())
                        .registMonth(dto.getRegistMonth())
                        .product(saved)
                        .build();
                ramSpecRepository.save(spec);
                break;
            }
            case "SSD": {
                var dto = requestDto.getSsdSpec();
                SsdSpec spec = SsdSpec.builder()
                        .productCategory(dto.getProductCategory())
                        .formFactor(dto.getFormFactor())
                        .interfaceType(dto.getInterfaceType())
                        .protocol(dto.getProtocol())
                        .ssdCapacity(dto.getSsdCapacity())
                        .memoryType(dto.getMemoryType())
                        .nandStructure(dto.getNandStructure())
                        .controller(dto.getController())
                        .ssdRead(dto.getSsdRead())
                        .ssdWrite(dto.getSsdWrite())
                        .mtbf(dto.getMtbf())
                        .tbw(dto.getTbw())
                        .nvmeHeatsink(dto.getNvmeHeatsink())
                        .width(dto.getWidth())
                        .height(dto.getHeight())
                        .thickness(dto.getThickness())
                        .weight(dto.getWeight())
                        .registYear(dto.getRegistYear())
                        .registMonth(dto.getRegistMonth())
                        .product(saved)
                        .build();
                ssdSpecRepository.save(spec);
                break;
            }
            case "HDD": {
                var dto = requestDto.getHddSpec();
                HddSpec spec = HddSpec.builder()
                        .productCategory(dto.getProductCategory())
                        .diskSize(dto.getDiskSize())
                        .hddCapacity(dto.getHddCapacity())
                        .interfaceType(dto.getInterfaceType())
                        .hddRpm(dto.getHddRpm())
                        .hddBuffer(dto.getHddBuffer())
                        .transferSpeed(dto.getTransferSpeed())
                        .recordingMethod(dto.getRecordingMethod())
                        .thickness(dto.getThickness())
                        .workload(dto.getWorkload())
                        .noise(dto.getNoise())
                        .registYear(dto.getRegistYear())
                        .registMonth(dto.getRegistMonth())
                        .product(saved)
                        .build();
                hddSpecRepository.save(spec);
                break;
            }
            default:
                // 등록할 스펙 없음
        }

        return ProductResponseDto.from(saved);
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
            if (gpuSpec == null) {
                gpuSpec = new GpuSpec();
                gpuSpec.setProduct(product);
            }
            gpuSpec.update(requestDto.getGpuSpec());
            gpuSpecRepository.save(gpuSpec);
        }

        if (requestDto.getRamSpec() != null) {
            RamSpec ramSpec = product.getRamSpec();
            if (ramSpec == null) {
                ramSpec = new RamSpec();
                ramSpec.setProduct(product);
            }
            ramSpec.update(requestDto.getRamSpec());
            ramSpecRepository.save(ramSpec);
        }

        if (requestDto.getSsdSpec() != null) {
            SsdSpec ssdSpec = product.getSsdSpec();
            if (ssdSpec == null) {
                ssdSpec = new SsdSpec();
                ssdSpec.setProduct(product);
            }
            ssdSpec.update(requestDto.getSsdSpec());
            ssdSpecRepository.save(ssdSpec);
        }

        if (requestDto.getHddSpec() != null) {
            HddSpec hddSpec = product.getHddSpec();
            if (hddSpec == null) {
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
