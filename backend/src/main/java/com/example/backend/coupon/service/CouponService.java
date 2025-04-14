package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.model.dto.request.AllCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.UserCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.response.CouponInfoDto;
import com.example.backend.coupon.model.dto.response.CouponListResponseDto;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;

    public Long CreateCouponForUser(UserCouponCreateRequestDto request){
        // 발급할 사용자 엔티티 찾기
        User user = userRepository.findById(request.getUserIdx()).orElse(null);
        // 적절한 쿠폰 정보 찾기
        Product product = productRepository.findById(request.getProductIdx()).orElse(null);
        // 요청이 잘못된 경우
        // TODO: 예외 처리를 다른 예외 클래스로...
        if (user == null || product == null) {
            throw new IllegalArgumentException("bad request");
        }
        Coupon coupon = couponRepository.findByCouponName(request.getCouponName()).orElse(null);
        String[] dates = request.getExpiryDate().split("-");
        if (coupon == null) { // 아직 한 번도 발급한 적 없는 종류의 쿠폰을 발급하는 경우
            LocalDateTime expiry = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])).atStartOfDay();
            coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(java.sql.Timestamp.valueOf(expiry)).product(product).build();
            couponRepository.save(coupon);
        }
        UserCoupon issuedCoupon = UserCoupon.builder().user(user).coupon(coupon).couponUsed(false).build();
        userCouponRepository.save(issuedCoupon);
        return issuedCoupon.getUserCouponIdx();
    }

    public List<Long> CreateCouponForAll(AllCouponCreateRequestDto request){
        // 발급할 사용자 엔티티 찾기
        List<User> users = userRepository.findAll();
        // 적절한 쿠폰 정보 찾기
        Product product = productRepository.findById(request.getProductIdx()).orElse(null);
        // 요청이 잘못된 경우
        // TODO: 예외 처리를 다른 예외 클래스로...
        if (product == null) {
            throw new IllegalArgumentException("bad request");
        }
        Coupon coupon = couponRepository.findByCouponName(request.getCouponName()).orElse(null);
        String[] dates = request.getExpiryDate().split("\\-");
        if (coupon == null) { // 아직 한 번도 발급한 적 없는 종류의 쿠폰을 발급하는 경우
            LocalDateTime expiry = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])).atStartOfDay();
            coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(java.sql.Timestamp.valueOf(expiry)).product(product).build();
            System.out.println(Arrays.toString(dates));
            System.out.println(coupon.getCouponValidDate());
            couponRepository.save(coupon);
        }
        List<Long> result = new ArrayList<>();
        for (User user : users) {
            UserCoupon issuedCoupon = UserCoupon.builder().user(user).coupon(coupon).couponUsed(false).build();
            userCouponRepository.save(issuedCoupon);
            result.add(issuedCoupon.getUserCouponIdx());
        }
        return result;
    }

    public CouponListResponseDto getCouponPage(Integer offset) {
        Page<Coupon> couponListInfo = couponRepository.findAll(PageRequest.of(offset,30));
        Long limit = couponListInfo.getTotalElements();
        Integer pageLength = couponListInfo.getTotalPages();
        List<CouponInfoDto> couponList = couponListInfo.getContent().stream()
                .map(coupon -> CouponInfoDto.from(coupon))
                .toList();
        return CouponListResponseDto.builder().couponList(couponList).total(pageLength).limit(limit).offset(offset).build();
    }
}
