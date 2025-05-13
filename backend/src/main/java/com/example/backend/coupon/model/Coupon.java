package com.example.backend.coupon.model;

import com.example.backend.coupon.model.dto.request.EventCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.UserCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.response.CouponInfoDto;
import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponIdx;
    private String couponName;
    private int couponDiscountRate;
    private Date couponValidDate;
    private Integer couponQuantity; // 남은 발급 가능 횟수
    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;
    // 유저 쿠폰과 일대다 맵핑
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> userCoupons;
    // Coupon.java
    public static Coupon of(String name, int quantity) {
        return Coupon.builder()
                .couponName(name)
                .couponQuantity(quantity)
                .build();
    }


    public CouponInfoDto toDto() {
        return CouponInfoDto.builder().couponIdx(this.couponIdx).couponName(this.couponName).couponDiscountRate(this.couponDiscountRate).productIdx(product.getProductIdx()).productName(product.getName()).couponStock(couponQuantity).couponValidDate(this.couponValidDate.toInstant().atZone(ZoneId.systemDefault())).build();
    }

    public void update(UserCouponCreateRequestDto dto) {
        couponName = dto.getCouponName();
        couponDiscountRate = dto.getDiscount();
        String[] dateString = dto.getExpiryDate().split("-");
        ZonedDateTime time = ZonedDateTime.of(LocalDate.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]),Integer.parseInt(dateString[2])).atStartOfDay(), ZoneId.systemDefault());
        couponValidDate = Date.from(time.toInstant());
    }

    public void updateEvent(EventCouponCreateRequestDto dto) {
        couponName = dto.getCouponName();
        couponDiscountRate = dto.getDiscount();
        String[] dateString = dto.getExpiryDate().split("-");
        ZonedDateTime time = ZonedDateTime.of(LocalDate.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]),Integer.parseInt(dateString[2])).atStartOfDay(), ZoneId.systemDefault());
        couponValidDate = Date.from(time.toInstant());
        couponQuantity = dto.getQuantity();
    }
}
