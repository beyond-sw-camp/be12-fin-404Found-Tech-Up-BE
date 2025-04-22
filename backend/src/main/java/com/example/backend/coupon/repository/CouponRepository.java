package com.example.backend.coupon.repository;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Page<Coupon> findAll(Pageable pageable);
    Optional<Coupon> findByCouponName(String couponName);

    List<Coupon> findAllByCouponNameContaining(String couponName); // 검색용 메서드
    List<Coupon> findAllByCouponQuantityGreaterThanEqual(Integer couponQuantity);
}
