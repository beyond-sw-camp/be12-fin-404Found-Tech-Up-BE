package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.model.dto.request.AllCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.EventCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.UserCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.response.CouponInfoDto;
import com.example.backend.coupon.model.dto.response.CouponListResponseDto;
import com.example.backend.coupon.model.dto.response.MyCouponInfoResponseDto;
import com.example.backend.coupon.repository.CouponRedisRepository;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.global.exception.CouponException;
import com.example.backend.global.exception.ProductException;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.CouponResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.notification.repository.UserNotificationRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final CouponRedisRepository couponRedisRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    private final AtomicBoolean serializationErrorLogged = new AtomicBoolean(false);
    private final Logger log = LoggerFactory.getLogger(CouponService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String couponLua;

    @PostConstruct
    private void init() {
        try {
            Resource resource = new ClassPathResource("lua/coupon_issue.lua");
            byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
            this.couponLua = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load Lua script: coupon_issue.lua", e);
        }
    }

    public CouponListResponseDto getEventList() {
        List<Coupon> coupons = couponRepository.findAllByCouponQuantityGreaterThanEqual(0);
        List<CouponInfoDto> couponInfoList = coupons.stream()
                .filter(c -> c.getCouponValidDate().toInstant().isAfter(ZonedDateTime.now().toInstant()))
                .map(Coupon::toDto)
                .collect(Collectors.toList());
        Collections.reverse(couponInfoList);
        return CouponListResponseDto.builder()
                .offset(0)
                .total(couponInfoList.size())
                .limit((long) couponInfoList.size())
                .couponList(couponInfoList)
                .build();
    }

    public List<MyCouponInfoResponseDto> getMyCouponList(User user) {
        return userCouponRepository.findAllByUser(user).stream()
                .map(UserCoupon::toDto)
                .toList();
    }

    // --------------------------- 관리자 전용 -------------------------

    public Long CreateCouponForUser(UserCouponCreateRequestDto request) {
        User user = userRepository.findById(request.getUserIdx())
                .orElseThrow(() -> new UserException(UserResponseStatus.INVALID_USER_ID));
        Product product = productRepository.findById(request.getProductIdx())
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        Coupon coupon = couponRepository.findByCouponName(request.getCouponName()).orElse(null);
        String[] dates = request.getExpiryDate().split("-");
        if (coupon == null) {
            ZonedDateTime expiry = LocalDate.of(
                            Integer.parseInt(dates[0]),
                            Integer.parseInt(dates[1]),
                            Integer.parseInt(dates[2])
                    ).atStartOfDay()
                    .atZone(ZoneOffset.ofHours(9));
            coupon = Coupon.builder()
                    .couponName(request.getCouponName())
                    .couponDiscountRate(request.getDiscount())
                    .couponValidDate(Date.from(expiry.toInstant()))
                    .couponQuantity(-1)
                    .product(product)
                    .build();
            couponRepository.save(coupon);
        }

        UserCoupon issuedCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .couponUsed(false)
                .build();
        userCouponRepository.save(issuedCoupon);

        String title = "이벤트 쿠폰 : " + coupon.getCouponName();
        String content = coupon.getCouponDiscountRate() + "% 할인, 만료일: " + coupon.getCouponValidDate();
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .notificationType(NotificationType.PERSONAL)
                .cronExpression("")
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        UserNotification userNotification = UserNotification.builder()
                .notificationType(NotificationType.PERSONAL)
                .user(user)
                .createdAt(LocalDateTime.now())
                .title(title)
                .content(content)
                .template(notification)
                .isRead(false)
                .build();
        userNotificationRepository.save(userNotification);

        return issuedCoupon.getUserCouponIdx();
    }

    public List<Long> CreateCouponForAll(AllCouponCreateRequestDto request) {
        List<User> users = userRepository.findAll();
        Product product = productRepository.findById(request.getProductIdx())
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        Coupon coupon = couponRepository.findByCouponName(request.getCouponName()).orElse(null);
        String[] dates = request.getExpiryDate().split("-");
        if (coupon == null) {
            ZonedDateTime expiry = LocalDate.of(
                            Integer.parseInt(dates[0]),
                            Integer.parseInt(dates[1]),
                            Integer.parseInt(dates[2])
                    ).atStartOfDay()
                    .atZone(ZoneOffset.ofHours(9));
            coupon = Coupon.builder()
                    .couponName(request.getCouponName())
                    .couponDiscountRate(request.getDiscount())
                    .couponValidDate(Date.from(expiry.toInstant()))
                    .couponQuantity(-1)
                    .product(product)
                    .build();
            couponRepository.save(coupon);
        }

        String title = "이벤트 쿠폰 : " + coupon.getCouponName();
        String content = coupon.getCouponDiscountRate() + "% 할인, 만료일: " + coupon.getCouponValidDate();
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .notificationType(NotificationType.PERSONAL)
                .cronExpression("")
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        List<Long> result = new ArrayList<>();
        for (User user : users) {
            UserCoupon issuedCoupon = UserCoupon.builder()
                    .user(user)
                    .coupon(coupon)
                    .couponUsed(false)
                    .build();
            userCouponRepository.save(issuedCoupon);
            result.add(issuedCoupon.getUserCouponIdx());

            UserNotification userNotification = UserNotification.builder()
                    .notificationType(NotificationType.PERSONAL)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .title(title)
                    .content(content)
                    .template(notification)
                    .isRead(false)
                    .build();
            userNotificationRepository.save(userNotification);
        }
        return result;
    }

    public CouponListResponseDto getCouponPage(Integer offset) {
        Page<Coupon> page = couponRepository.findAll(PageRequest.of(offset, 30));
        List<CouponInfoDto> content = page.getContent().stream()
                .map(CouponInfoDto::from)
                .toList();
        return CouponListResponseDto.builder()
                .couponList(content)
                .total(page.getTotalPages())
                .limit(page.getTotalElements())
                .offset(offset)
                .build();
    }

    public CouponListResponseDto getCouponList() {
        List<CouponInfoDto> list = couponRepository.findAll().stream()
                .map(CouponInfoDto::from)
                .toList();
        return CouponListResponseDto.builder()
                .couponList(list)
                .total(list.size())
                .limit((long) list.size())
                .offset(0)
                .build();
    }

    public CouponInfoDto getCouponInfo(Long couponIdx) {
        Coupon coupon = couponRepository.findById(couponIdx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        return CouponInfoDto.from(coupon);
    }

    @Transactional
    public void updateCoupon(Long idx, UserCouponCreateRequestDto request) {
        Coupon coupon = couponRepository.findById(idx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        coupon.update(request);
        couponRepository.save(coupon);
    }

    @Transactional
    public Boolean deleteCoupon(Long couponIdx) {
        Coupon coupon = couponRepository.findById(couponIdx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        if (coupon.getUserCoupons().stream().anyMatch(UserCoupon::getCouponUsed)) {
            return false;
        }
        userCouponRepository.deleteAll(coupon.getUserCoupons());
        couponRepository.delete(coupon);
        return true;
    }

    public CouponListResponseDto searchCoupon(String keyword) {
        List<CouponInfoDto> result = couponRepository.findAllByCouponNameContaining(keyword).stream()
                .map(Coupon::toDto)
                .toList();
        return CouponListResponseDto.builder()
                .couponList(result)
                .total(result.size())
                .limit((long) result.size())
                .offset(0)
                .build();
    }

    /**
     * 이벤트 쿠폰 발급 (Lua 스크립트 사용)
     */
    public boolean issueEventCoupon(User user, Long couponId) {
        // 해시 태그(tag)를 묶어 같은 슬롯으로 보냅니다.
        String tag           = "{" + couponId + "}";
        String userSetKey    = "set.receive.couponId." + tag;
        String stockHashKey  = "hash.coupon.stock."   + tag;
        String listKey       = "list.received.user."  + tag;

        // 키 순서대로
        List<Object> keys = List.of(userSetKey, stockHashKey, listKey);

        // 사용자 식별자
        String userIdx = user.getUserIdx().toString();

        // 스크립트 실행 (MULTI 리턴)
        List<Object> raw = redissonClient.getScript()
                .eval(RScript.Mode.READ_WRITE,
                        couponLua,
                        RScript.ReturnType.MULTI,
                        keys,
                        userIdx);

        // 안전하게 Number → long 변환
        long code   = ((Number) raw.get(0)).longValue();  // 1=성공, 0=중복, -1=재고부족
        long remain = ((Number) raw.get(1)).longValue();

        if (code == 1L) {
            // Redis 성공 후에만 DB에 기록
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));

            UserCoupon uc = UserCoupon.builder()
                    .user(user)
                    .coupon(coupon)
                    .couponUsed(false)
                    .build();
            userCouponRepository.save(uc);
            return true;
        }
        // 중복(0) 또는 재고부족(-1)
        return false;
    }



    @Transactional
    public void createEvent(EventCouponCreateRequestDto request) {
        Product product = productRepository.findById(request.getProductIdx())
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        String[] dates = request.getExpiryDate().split("-");
        ZonedDateTime expiry = LocalDate.of(
                        Integer.parseInt(dates[0]),
                        Integer.parseInt(dates[1]),
                        Integer.parseInt(dates[2])
                ).atStartOfDay()
                .atZone(ZoneOffset.ofHours(9));
        Coupon coupon = Coupon.builder()
                .couponName(request.getCouponName())
                .couponDiscountRate(request.getDiscount())
                .couponValidDate(Date.from(expiry.toInstant()))
                .couponQuantity(request.getQuantity())
                .product(product)
                .build();
        couponRepository.save(coupon);
    }

    @Transactional
    public void updateEvent(Long eventIdx, EventCouponCreateRequestDto request) {
        Coupon event = couponRepository.findById(eventIdx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        event.updateEvent(request);
        couponRepository.save(event);
    }

    @Transactional
    public void forceDeleteEvent(Long eventIdx) {
        Coupon coupon = couponRepository.findById(eventIdx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        List<UserCoupon> issued = coupon.getUserCoupons().stream()
                .filter(u -> !u.getCouponUsed())
                .toList();
        userCouponRepository.deleteAll(issued);
        couponRepository.delete(coupon);
    }

    // Redis 상의 남은 수량 조회
    public Long getRemainingQuantity(Long couponId) {
        String stockHashKey = "hash.coupon.stock." + couponId;
        return couponRedisRepository.hGet(stockHashKey, "quantity");
    }

    // Redis 상의 발급된 유저 수 조회
    public long countIssuedUsers(Long couponId) {
        String userSetKey = "set.receive.couponId." + couponId;
        return couponRedisRepository.sCard(userSetKey);
    }
}
