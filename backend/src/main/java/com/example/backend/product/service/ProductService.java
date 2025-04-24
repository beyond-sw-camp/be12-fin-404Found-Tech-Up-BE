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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public Page<ProductResponseDto> getProductList(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDto::from);
    }

    public ProductResponseDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        return ProductResponseDto.from(product);
    }

    public Page<ProductResponseDto> searchProduct(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable)
                .map(ProductResponseDto::from);
    }

    public Page<ProductResponseDto> filterProduct(ProductFilterRequestDto dto, Pageable pageable) {
        if (dto.getCategory() != null) {
            return productRepository
                    .findAllByCategoryIgnoreCase(dto.getCategory(), pageable)
                    .map(ProductResponseDto::from);
        }
        List<ProductResponseDto> all = productRepository.findAll().stream()
                .filter(product -> {
                    String cat = product.getCategory() != null
                            ? product.getCategory().toUpperCase()
                            : "";

                    return switch (cat) {
                        case "CPU" -> {
                            var s = product.getCpuSpec();
                            if (s == null) yield false;
                            yield (
                                    (dto.getAmdCpuType() == null || s.getAmdCpuType().equalsIgnoreCase(dto.getAmdCpuType())) &&
                                            (dto.getSocketType() == null || s.getSocketType().equalsIgnoreCase(dto.getSocketType())) &&
                                            (dto.getCoreCount() == null || s.getCoreCount().equals(dto.getCoreCount())) &&
                                            (dto.getThreadCount() == null || s.getThreadCount().equals(dto.getThreadCount())) &&
                                            (dto.getCpuMemorySpec() == null || s.getMemorySpec().equalsIgnoreCase(dto.getMemorySpec())) &&
                                            (dto.getBuiltInGraphic() == null || s.getBuiltInGraphic().equalsIgnoreCase(dto.getBuiltInGraphic())) &&
                                            (dto.getManufactoringProcess() == null || s.getManufactoringProcess().equalsIgnoreCase(dto.getManufactoringProcess())) &&
                                            (dto.getBaseClock() == null || s.getBaseClock().equalsIgnoreCase(dto.getBaseClock())) &&
                                            (dto.getMaxClock() == null || s.getMaxClock().equalsIgnoreCase(dto.getMaxClock())) &&
                                            (dto.getL2Cache() == null || s.getL2Cache().equalsIgnoreCase(dto.getL2Cache())) &&
                                            (dto.getL3Cache() == null || s.getL3Cache().equalsIgnoreCase(dto.getL3Cache())) &&
                                            (dto.getOperateSystem() == null || s.getOperateSystem().equalsIgnoreCase(dto.getOperateSystem())) &&
                                            (dto.getTdp() == null || s.getTdp().equalsIgnoreCase(dto.getTdp())) &&
                                            (dto.getPpt() == null || s.getPpt().equalsIgnoreCase(dto.getPpt())) &&
                                            (dto.getPcieVer() == null || s.getPcieVer().equalsIgnoreCase(dto.getPcieVer())) &&
                                            (dto.getMaxPcie() == null || s.getMaxPcie().equalsIgnoreCase(dto.getMaxPcie())) &&
                                            (dto.getMaxMemorySize() == null || s.getMaxMemorySize().equalsIgnoreCase(dto.getMaxMemorySize())) &&
                                            (dto.getCpuMemoryClock() == null || s.getMemoryClock().equalsIgnoreCase(dto.getMemoryClock())) &&
                                            (dto.getMemoryChannel() == null || s.getMemoryChannel().equals(dto.getMemoryChannel())) &&
                                            (dto.getPakageType() == null || s.getPakageType().equalsIgnoreCase(dto.getPakageType())) &&
                                            (dto.getCooler() == null || s.getCooler().equalsIgnoreCase(dto.getCooler())) &&
                                            (dto.getCpuRegistYear() == null || s.getRegistYear().equals(dto.getCpuRegistYear())) &&
                                            (dto.getCpuRegistMonth() == null || s.getRegistMonth().equals(dto.getCpuRegistMonth()))
                            );
                        }
                        case "GPU" -> {
                            var s = product.getGpuSpec();
                            if (s == null) yield false;
                            yield (
                                    (dto.getChipsetManufacturer() == null || s.getChipsetManufacturer().equalsIgnoreCase(dto.getChipsetManufacturer())) &&
                                            (dto.getProductSeries() == null || s.getProductSeries().equalsIgnoreCase(dto.getProductSeries())) &&
                                            (dto.getGpuManufacturingProcess() == null || s.getGpuManufacturingProcess().equalsIgnoreCase(dto.getGpuManufacturingProcess())) &&
                                            (dto.getGpuInterfaceType() == null || s.getInterfaceType().equalsIgnoreCase(dto.getGpuInterfaceType())) &&
                                            (dto.getRecommendedPowerCapacity() == null || s.getRecommendedPowerCapacity().equalsIgnoreCase(dto.getRecommendedPowerCapacity())) &&
                                            (dto.getPowerPort() == null || s.getPowerPort().equalsIgnoreCase(dto.getPowerPort())) &&
                                            (dto.getGpuLength() == null || s.getGpuLength().equals(dto.getGpuLength())) &&
                                            (dto.getBoostClock() == null || s.getBoostClock().equalsIgnoreCase(dto.getBoostClock())) &&
                                            (dto.getStreamProcessor() == null || s.getStreamProcessor().equalsIgnoreCase(dto.getStreamProcessor())) &&
                                            (dto.getGpuMemoryType() == null || s.getMemoryType().equalsIgnoreCase(dto.getMemoryType())) &&
                                            (dto.getMemoryClock() == null || s.getMemoryClock().equalsIgnoreCase(dto.getMemoryClock())) &&
                                            (dto.getGpuMemory() == null || s.getGpuMemory().equals(dto.getGpuMemory())) &&
                                            (dto.getMemoryBus() == null || s.getMemoryBus().equalsIgnoreCase(dto.getMemoryBus())) &&
                                            (dto.getHdmi() == null || s.getHdmi().equalsIgnoreCase(dto.getHdmi())) &&
                                            (dto.getDisplayPort() == null || s.getDisplayPort().equalsIgnoreCase(dto.getDisplayPort())) &&
                                            (dto.getMonitorSupport() == null || s.getMonitorSupport().equalsIgnoreCase(dto.getMonitorSupport())) &&
                                            (dto.getFanCount() == null || s.getFanCount().equals(dto.getFanCount())) &&
                                            (dto.getGpuThickness() == null || s.getThickness().equalsIgnoreCase(dto.getGpuThickness())) &&
                                            (dto.getGpuRegistYear() == null || s.getRegistYear().equals(dto.getGpuRegistYear())) &&
                                            (dto.getGpuRegistMonth() == null || s.getRegistMonth().equals(dto.getGpuRegistMonth()))
                            );
                        }
                        case "RAM" -> {
                            var s = product.getRamSpec();
                            if (s == null) yield false;
                            yield (
                                    (dto.getUsageDevice() == null || s.getUsageDevice().equalsIgnoreCase(dto.getUsageDevice())) &&
                                            (dto.getRamProductCategory() == null || s.getProductCategory().equalsIgnoreCase(dto.getRamProductCategory())) &&
                                            (dto.getMemorySpec() == null || s.getMemorySpec().equalsIgnoreCase(dto.getMemorySpec())) &&
                                            (dto.getRamSize() == null || s.getRamSize().equals(dto.getRamSize())) &&
                                            (dto.getOperatingClock() == null || s.getOperatingClock().equalsIgnoreCase(dto.getOperatingClock())) &&
                                            (dto.getRamTiming() == null || s.getRamTiming().equalsIgnoreCase(dto.getRamTiming())) &&
                                            (dto.getOperatingVoltage() == null || s.getOperatingVoltage().equalsIgnoreCase(dto.getOperatingVoltage())) &&
                                            (dto.getRamNum() == null || s.getRamNum().equals(dto.getRamNum())) &&
                                            (dto.getHeatsink() == null || s.getHeatsink().equalsIgnoreCase(dto.getHeatsink())) &&
                                            (dto.getRamHeight() == null || s.getHeight().equals(dto.getHeight())) &&
                                            (dto.getRamRegistYear() == null || s.getRegistYear().equals(dto.getRamRegistYear())) &&
                                            (dto.getRamRegistMonth() == null || s.getRegistMonth().equals(dto.getRamRegistMonth()))
                            );
                        }
                        case "SSD" -> {
                            var s = product.getSsdSpec();
                            if (s == null) yield false;
                            yield (
                                    (dto.getSsdProductCategory() == null || s.getProductCategory().equalsIgnoreCase(dto.getSsdProductCategory())) &&
                                            (dto.getFormFactor() == null || s.getFormFactor().equalsIgnoreCase(dto.getFormFactor())) &&
                                            (dto.getSsdInterfaceType() == null || s.getInterfaceType().equalsIgnoreCase(dto.getSsdInterfaceType())) &&
                                            (dto.getProtocol() == null || s.getProtocol().equalsIgnoreCase(dto.getProtocol())) &&
                                            (dto.getSsdCapacity() == null || s.getSsdCapacity().equals(dto.getSsdCapacity())) &&
                                            (dto.getMemoryType() == null || s.getMemoryType().equalsIgnoreCase(dto.getMemoryType())) &&
                                            (dto.getNandStructure() == null || s.getNandStructure().equalsIgnoreCase(dto.getNandStructure())) &&
                                            (dto.getController() == null || s.getController().equalsIgnoreCase(dto.getController())) &&
                                            (dto.getSsdRead() == null || s.getSsdRead().equals(dto.getSsdRead())) &&
                                            (dto.getSsdWrite() == null || s.getSsdWrite().equals(dto.getSsdWrite())) &&
                                            (dto.getMtbf() == null || s.getMtbf().equalsIgnoreCase(dto.getMtbf())) &&
                                            (dto.getTbw() == null || s.getTbw().equalsIgnoreCase(dto.getTbw())) &&
                                            (dto.getNvmeHeatsink() == null || s.getNvmeHeatsink().equalsIgnoreCase(dto.getNvmeHeatsink())) &&
                                            (dto.getWidth() == null || s.getWidth().equals(dto.getWidth())) &&
                                            (dto.getHeight() == null || s.getHeight().equals(dto.getHeight())) &&
                                            (dto.getSsdThickness() == null || s.getThickness().equalsIgnoreCase(dto.getSsdThickness())) &&
                                            (dto.getWeight() == null || s.getWeight().equals(dto.getWeight())) &&
                                            (dto.getSsdRegistYear() == null || s.getRegistYear().equals(dto.getSsdRegistYear())) &&
                                            (dto.getSsdRegistMonth() == null || s.getRegistMonth().equals(dto.getSsdRegistMonth()))
                            );
                        }
                        case "HDD" -> {
                            var s = product.getHddSpec();
                            if (s == null) yield false;
                            yield (
                                    (dto.getHddProductCategory() == null || s.getProductCategory().equalsIgnoreCase(dto.getHddProductCategory())) &&
                                            (dto.getDiskSize() == null || s.getDiskSize().equalsIgnoreCase(dto.getDiskSize())) &&
                                            (dto.getHddCapacity() == null || s.getHddCapacity().equals(dto.getHddCapacity())) &&
                                            (dto.getHddInterfaceType() == null || s.getInterfaceType().equalsIgnoreCase(dto.getHddInterfaceType())) &&
                                            (dto.getHddRpm() == null || s.getHddRpm().equals(dto.getHddRpm())) &&
                                            (dto.getHddBuffer() == null || s.getHddBuffer().equals(dto.getHddBuffer())) &&
                                            (dto.getTransferSpeed() == null || s.getTransferSpeed().equalsIgnoreCase(dto.getTransferSpeed())) &&
                                            (dto.getRecordingMethod() == null || s.getRecordingMethod().equalsIgnoreCase(dto.getRecordingMethod())) &&
                                            (dto.getHddThickness() == null || s.getThickness().equalsIgnoreCase(dto.getHddThickness())) &&
                                            (dto.getWorkload() == null || s.getWorkload().equalsIgnoreCase(dto.getWorkload())) &&
                                            (dto.getNoise() == null || s.getNoise().equalsIgnoreCase(dto.getNoise())) &&
                                            (dto.getHddRegistYear() == null || s.getRegistYear().equals(dto.getHddRegistYear())) &&
                                            (dto.getHddRegistMonth() == null || s.getRegistMonth().equals(dto.getHddRegistMonth()))
                            );
                        }
                        default -> true;
                    };
                })
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());

        int total = all.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end   = Math.min(start + pageable.getPageSize(), total);

        List<ProductResponseDto> pageContent = start > total
                ? Collections.emptyList()
                : all.subList(start, end);

        return new PageImpl<>(pageContent, pageable, total);
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
