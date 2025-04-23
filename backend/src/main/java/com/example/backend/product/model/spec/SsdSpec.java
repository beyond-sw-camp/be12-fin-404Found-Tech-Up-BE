package com.example.backend.product.model.spec;

import com.example.backend.product.model.Product;
import com.example.backend.product.model.dto.SsdSpecDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SsdSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ssdIdx;
    @Column(name = "제품분류")
    private String productCategory;

    @Column(name = "폼팩터")
    private String formFactor;

    @Column(name = "인터페이스")
    private String interfaceType;

    @Column(name = "프로토콜")
    private String protocol;

    @Column(name = "용량")
    private String ssdCapacity;

    @Column(name = "메모리 타입")
    private String memoryType;

    @Column(name = "낸드 구조")
    private String nandStructure;

    @Column(name = "컨트롤러")
    private String controller;

    @Column(name = "순차읽기")
    private String ssdRead;

    @Column(name = "순차쓰기")
    private String ssdWrite;

    @Column(name = "MTBF")
    private String mtbf;

    @Column(name = "TBW")
    private String tbw;

    @Column(name = "NVMe 방열판")
    private String nvmeHeatsink;

    @Column(name = "가로")
    private String width;

    @Column(name = "세로")
    private String height;

    @Column(name = "두께")
    private String thickness;

    @Column(name = "무게")
    private String weight;

    @Column(name = "등록년")
    private String registYear;

    @Column(name = "등록월")
    private String registMonth;

    @OneToOne
    @JoinColumn(name="product_idx")
    private Product product;

    public void update(SsdSpecDto ssdSpecDto) {
        this.productCategory = ssdSpecDto.getProductCategory();
        this.formFactor = ssdSpecDto.getFormFactor();
        this.interfaceType = ssdSpecDto.getInterfaceType();
        this.protocol = ssdSpecDto.getProtocol();
        this.ssdCapacity = ssdSpecDto.getSsdCapacity();
        this.memoryType = ssdSpecDto.getMemoryType();
        this.nandStructure = ssdSpecDto.getNandStructure();
        this.controller = ssdSpecDto.getController();
        this.ssdRead = ssdSpecDto.getSsdRead();
        this.ssdWrite = ssdSpecDto.getSsdWrite();
        this.mtbf = ssdSpecDto.getMtbf();
        this.tbw = ssdSpecDto.getTbw();
        this.nvmeHeatsink = ssdSpecDto.getNvmeHeatsink();
        this.width = ssdSpecDto.getWidth();
        this.height = ssdSpecDto.getHeight();
        this.thickness = ssdSpecDto.getThickness();
        this.weight = ssdSpecDto.getWeight();
        this.registYear = ssdSpecDto.getRegistYear();
        this.registMonth = ssdSpecDto.getRegistMonth();
    }
}
