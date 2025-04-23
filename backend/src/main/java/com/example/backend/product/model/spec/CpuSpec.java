package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.CpuSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CpuSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cpuIdx;
    @Column(name = "AMD CPU종류")
    private String amdCpuType;
    @Column(name = "소켓 구분")
    private String socketType;
    @Column(name = "코어 수")
    private String coreCount;
    @Column(name = "스레드 수")
    private String threadCount;
    @Column(name = "메모리 규격")
    private String memorySpec;
    @Column(name = "내장그래픽")
    private String builtInGraphic;
    @Column(name = "제조 공정")
    private String manufactoringProcess;
    @Column(name = "기본 클럭")
    private String baseClock;
    @Column(name = "최대 클럭")
    private String maxClock;
    @Column(name = "L2 캐시")
    private String l2Cache;
    @Column(name = "L3 캐시")
    private String l3Cache;
    @Column(name = "연산 체계")
    private String operateSystem;
    @Column(name = "TDP")
    private String tdp;
    @Column(name = "PPT")
    private String ppt;
    @Column(name = "PCIe 버전")
    private String pcieVer;
    @Column(name = "최대 PCIe 레인 수")
    private String maxPcie;
    @Column(name = "최대 메모리 크기")
    private String maxMemorySize;
    @Column(name = "메모리 클럭")
    private String memoryClock;
    @Column(name = "메모리 채널")
    private Integer memoryChannel;
    @Column(name = "패키지 형태")
    private String pakageType;
    @Column(name = "쿨러")
    private String cooler;
    @Column(name = "등록년")
    private String registYear;
    @Column(name = "등록월")
    private String registMonth;


    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(CpuSpecDto cpuSpecDto) {
        this.amdCpuType = cpuSpecDto.getAmdCpuType();
        this.socketType = cpuSpecDto.getSocketType();
        this.coreCount = cpuSpecDto.getCoreCount();
        this.threadCount = cpuSpecDto.getThreadCount();
        this.memorySpec = cpuSpecDto.getMemorySpec();
        this.builtInGraphic = cpuSpecDto.getBuiltInGraphic();
        this.manufactoringProcess = cpuSpecDto.getManufactoringProcess();
        this.baseClock = cpuSpecDto.getBaseClock();
        this.maxClock = cpuSpecDto.getMaxClock();
        this.l2Cache = cpuSpecDto.getL2Cache();
        this.l3Cache = cpuSpecDto.getL3Cache();
        this.operateSystem = cpuSpecDto.getOperateSystem();
        this.tdp = cpuSpecDto.getTdp();
        this.ppt = cpuSpecDto.getPpt();
        this.pcieVer = cpuSpecDto.getPcieVer();
        this.maxPcie = cpuSpecDto.getMaxPcie();
        this.maxMemorySize = cpuSpecDto.getMaxMemorySize();
        this.memoryClock = cpuSpecDto.getMemoryClock();
        this.memoryChannel = cpuSpecDto.getMemoryChannel();
        this.pakageType = cpuSpecDto.getPakageType();
        this.cooler = cpuSpecDto.getCooler();
        this.registYear = cpuSpecDto.getRegistYear();
        this.registMonth = cpuSpecDto.getRegistMonth();
    }
}
