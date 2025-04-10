package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
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
        if (coupon == null) { // 아직 한 번도 발급한 적 없는 종류의 쿠폰을 발급하는 경우
            coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(new Date(request.getExpiryDate())).build();
            couponRepository.save(coupon);
        }
        UserCoupon issuedCoupon = UserCoupon.builder().user(user).coupon(coupon).couponUsed(false).build();
        userCouponRepository.save(issuedCoupon);
        return issuedCoupon.getUserCouponIdx();
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
