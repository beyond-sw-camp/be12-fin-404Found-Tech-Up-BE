package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.GpuSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpuIdx;
    @Column(name = "칩셋 제조사")
    private String chipsetManufacturer;

    @Column(name = "제품 시리즈")
    private String productSeries;

    @Column(name = "GPU 제조 공정")
    private String gpuManufacturingProcess;

    @Column(name = "인터페이스")
    private String interfaceType;

    @Column(name = "권장 파워용량")
    private String recommendedPowerCapacity;

    @Column(name = "전원 포트")
    private String powerPort;

    @Column(name = "가로(길이)")
    private String gpuLength;

    @Column(name = "부스트클럭")
    private String boostClock;

    @Column(name = "스트림 프로세서")
    private String streamProcessor;

    @Column(name = "메모리 종류")
    private String memoryType;

    @Column(name = "메모리 클럭")
    private String memoryClock;

    @Column(name = "메모리 용량")
    private String gpuMemory;

    @Column(name = "메모리 버스")
    private String memoryBus;

    @Column(name = "HDMI")
    private String hdmi;

    @Column(name = "DisplayPort")
    private String displayPort;

    @Column(name = "모니터 지원")
    private String monitorSupport;

    @Column(name = "팬 개수")
    private String fanCount;

    @Column(name = "두께")
    private String thickness;

    @Column(name = "등록년")
    private String registYear;

    @Column(name = "등록월")
    private String registMonth;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(GpuSpecDto gpuSpecDto) {
        this.chipsetManufacturer = gpuSpecDto.getChipsetManufacturer();
        this.productSeries = gpuSpecDto.getProductSeries();
        this.gpuManufacturingProcess = gpuSpecDto.getGpuManufacturingProcess();
        this.interfaceType = gpuSpecDto.getInterfaceType();
        this.recommendedPowerCapacity = gpuSpecDto.getRecommendedPowerCapacity();
        this.powerPort = gpuSpecDto.getPowerPort();
        this.gpuLength = gpuSpecDto.getGpuLength();
        this.boostClock = gpuSpecDto.getBoostClock();
        this.streamProcessor = gpuSpecDto.getStreamProcessor();
        this.memoryType = gpuSpecDto.getMemoryType();
        this.memoryClock = gpuSpecDto.getMemoryClock();
        this.gpuMemory = gpuSpecDto.getGpuMemory();
        this.memoryBus = gpuSpecDto.getMemoryBus();
        this.hdmi = gpuSpecDto.getHdmi();
        this.displayPort = gpuSpecDto.getDisplayPort();
        this.monitorSupport = gpuSpecDto.getMonitorSupport();
        this.fanCount = gpuSpecDto.getFanCount();
        this.thickness = gpuSpecDto.getThickness();
        this.registYear = gpuSpecDto.getRegistYear();
        this.registMonth = gpuSpecDto.getRegistMonth();
    }
}
