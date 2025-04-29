package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.HddSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HddSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hddIdx;
    @Column(name = "제품 분류")
    private String productCategory;

    @Column(name = "디스크 크기")
    private String diskSize;

    @Column(name = "디스크 용량")
    private String hddCapacity;

    @Column(name = "인터페이스")
    private String interfaceType;

    @Column(name = "회전수")
    private String hddRpm;

    @Column(name = "버퍼 용량")
    private String hddBuffer;

    @Column(name = "전송 속도")
    private String transferSpeed;

    @Column(name = "기록방식")
    private String recordingMethod;

    @Column(name = "두께")
    private String thickness;

    @Column(name = "작업부하량")
    private String workload;

    @Column(name = "소음(유휴/탐색)")
    private String noise;

    @Column(name = "등록년")
    private String registYear;

    @Column(name = "등록월")
    private String registMonth;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(HddSpecDto hddSpecDto) {
        this.productCategory = hddSpecDto.getProductCategory();
        this.diskSize = hddSpecDto.getDiskSize();
        this.hddCapacity = hddSpecDto.getHddCapacity();
        this.interfaceType = hddSpecDto.getInterfaceType();
        this.hddRpm = hddSpecDto.getHddRpm();
        this.hddBuffer = hddSpecDto.getHddBuffer();
        this.transferSpeed = hddSpecDto.getTransferSpeed();
        this.recordingMethod = hddSpecDto.getRecordingMethod();
        this.thickness = hddSpecDto.getThickness();
        this.workload = hddSpecDto.getWorkload();
        this.noise = hddSpecDto.getNoise();
        this.registYear = hddSpecDto.getRegistYear();
        this.registMonth = hddSpecDto.getRegistMonth();
    }
}
