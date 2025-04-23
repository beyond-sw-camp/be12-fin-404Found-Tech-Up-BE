package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.RamSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RamSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ramIdx;
    @Column(name = "사용 장치")
    private String usageDevice;

    @Column(name = "제품 분류")
    private String productCategory;

    @Column(name = "메모리 규격")
    private String memorySpec;

    @Column(name = "메모리 용량")
    private String ramSize;

    @Column(name = "동작클럭(대역폭)")
    private String operatingClock;

    @Column(name = "램타이밍")
    private String ramTiming;

    @Column(name = "동작전압")
    private String operatingVoltage;

    @Column(name = "램개수")
    private String ramNum;

    @Column(name = "히트싱크")
    private String heatsink;

    @Column(name = "높이")
    private String height;

    @Column(name = "등록년")
    private String registYear;

    @Column(name = "등록월")
    private String registMonth;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(RamSpecDto ramSpecDto) {
        this.usageDevice = ramSpecDto.getUsageDevice();
        this.productCategory = ramSpecDto.getProductCategory();
        this.memorySpec = ramSpecDto.getMemorySpec();
        this.ramSize = ramSpecDto.getRamSize();
        this.operatingClock = ramSpecDto.getOperatingClock();
        this.ramTiming = ramSpecDto.getRamTiming();
        this.operatingVoltage = ramSpecDto.getOperatingVoltage();
        this.ramNum = ramSpecDto.getRamNum();
        this.heatsink = ramSpecDto.getHeatsink();
        this.height = ramSpecDto.getHeight();
        this.registYear = ramSpecDto.getRegistYear();
        this.registMonth = ramSpecDto.getRegistMonth();
    }
}
