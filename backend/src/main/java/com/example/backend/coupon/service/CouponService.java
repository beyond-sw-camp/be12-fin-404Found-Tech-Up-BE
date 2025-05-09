package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.CouponRedisEntity;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.model.dto.request.AllCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.EventCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.EventCouponIssueRequestDto;
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
import com.example.backend.global.response.BaseResponseServiceImpl;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
    Note: 쿠폰 만료일 설정시 시간대에 주의하세요: 서울 기준 +9시간 필요하므로 ZonedDateTime을 잘 설정해야 합니다.
 */

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

    private final CouponDBService couponDBService;

    private final CouponCacheService couponCacheService;

    private final RedissonClient redissonClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CouponListResponseDto getEventList() {
        List<Coupon> coupons = couponRepository.findAllByCouponQuantityGreaterThanEqual(0);
        // 만료 기한 끝난 이벤트는 제거
        List<CouponInfoDto> couponInfoList = new ArrayList<>(coupons.stream().filter(value -> value.getCouponValidDate().toInstant().compareTo(ZonedDateTime.now().toInstant()) > 0).map(Coupon::toDto).toList());
        Collections.reverse(couponInfoList);
        return CouponListResponseDto.builder().offset(0).total(couponInfoList.size()).limit((long) couponInfoList.size()).couponList(couponInfoList).build();
    }

    public List<MyCouponInfoResponseDto> getMyCouponList(User user) {
        List<UserCoupon> myCoupons = userCouponRepository.findAllByUser(user);
        return myCoupons.stream().map(UserCoupon::toDto).toList();
    }

    // --------------------------- 관리자 전용 -------------------------

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
            ZonedDateTime expiry = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])).atStartOfDay().atZone(ZoneOffset.ofHours(9)); // +09:00
            coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(Date.from(expiry.toInstant())).couponQuantity(-1).product(product).build();
            couponRepository.save(coupon);
        }
        UserCoupon issuedCoupon = UserCoupon.builder().user(user).coupon(coupon).couponUsed(false).build();
        userCouponRepository.save(issuedCoupon);
        // 알림
        String title = "이벤트 쿠폰 :"+ coupon.getCouponName();
        String content = coupon.getCouponDiscountRate()+"% 할인, 만료일: "+ coupon.getCouponValidDate();
        Notification notification = Notification.builder().title(title).content(content).notificationType(NotificationType.PERSONAL).cronExpression("").createdAt(LocalDateTime.now()).build();
        notificationRepository.save(notification);
        UserNotification userNotification = UserNotification.builder().notificationType(NotificationType.PERSONAL).user(user).createdAt(LocalDateTime.now()).title(title).content(content).template(notification).isRead(false).build();
        userNotificationRepository.save(userNotification);

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
            ZonedDateTime expiry = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])).atStartOfDay().atZone(ZoneOffset.ofHours(9)); // +09:00
            coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(Date.from(expiry.toInstant())).couponQuantity(-1).product(product).build();
            couponRepository.save(coupon);
        }
        String title = "이벤트 쿠폰 :"+ coupon.getCouponName();
        String content = coupon.getCouponDiscountRate()+"% 할인, 만료일: "+ coupon.getCouponValidDate();
        // 알림 템플릿 생성
        Notification notification = Notification.builder().title(title).content(content).notificationType(NotificationType.PERSONAL).cronExpression("").createdAt(LocalDateTime.now()).build();
        notificationRepository.save(notification);
        List<Long> result = new ArrayList<>();
        for (User user : users) {
            // 사용자 별 쿠폰 발급
            UserCoupon issuedCoupon = UserCoupon.builder().user(user).coupon(coupon).couponUsed(false).build();
            userCouponRepository.save(issuedCoupon);
            result.add(issuedCoupon.getUserCouponIdx());
            // 쿠폰 생성 알림
            UserNotification userNotification = UserNotification.builder().notificationType(NotificationType.PERSONAL).user(user).createdAt(LocalDateTime.now()).title(title).content(content).template(notification).isRead(false).build();
            userNotificationRepository.save(userNotification);
        }
        return result;
    }

    public CouponListResponseDto getCouponPage(Integer offset) {
        Page<Coupon> couponListInfo = couponRepository.findAll(PageRequest.of(offset,30));
        Long limit = couponListInfo.getTotalElements();
        Integer pageLength = couponListInfo.getTotalPages();
        List<CouponInfoDto> couponList = couponListInfo.getContent().stream()
                .map(CouponInfoDto::from)
                .toList();
        return CouponListResponseDto.builder().couponList(couponList).total(pageLength).limit(limit).offset(offset).build();
    }
    public CouponListResponseDto getCouponList() {
        List<Coupon> couponListInfo = couponRepository.findAll();
        Long limit = (long) couponListInfo.size();
        Integer pageLength = couponListInfo.size();
        List<CouponInfoDto> couponList = couponListInfo.stream().map(coupon -> CouponInfoDto.from(coupon))
                .toList();
        return CouponListResponseDto.builder().couponList(couponList).total(pageLength).limit(limit).offset(0).build();
    }

    public CouponInfoDto getCouponInfo(Long couponIdx) {
        Coupon coupon = couponRepository.findById(couponIdx).orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        return CouponInfoDto.from(coupon);
    }
    @Transactional
    public void updateCoupon(Long idx, UserCouponCreateRequestDto request) {
        Coupon coupon = couponRepository.findById(idx).orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        coupon.update(request);
        couponRepository.save(coupon);
    }
    @Transactional
    public Boolean deleteCoupon(Long couponIdx) {
        Coupon coupon = couponRepository.findById(couponIdx).orElseThrow(()-> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        List<UserCoupon> issuedCoupons = coupon.getUserCoupons();
        for (UserCoupon issuedCoupon : issuedCoupons) {
            if (issuedCoupon.getCouponUsed()) return false;
        }
        userCouponRepository.deleteAll(issuedCoupons);
        couponRepository.delete(coupon);
        return true;
    }

    public CouponListResponseDto searchCoupon(String keyword) {
        List<Coupon> couponList = couponRepository.findAllByCouponNameContaining(keyword);
        Long limit = (long) couponList.size();
        Integer pageLength = couponList.size();
        List<CouponInfoDto> result = couponList.stream().map(Coupon::toDto).toList();
        return CouponListResponseDto.builder().couponList(result).total(pageLength).limit(limit).offset(0).build();
    }

    /*
    @Transactional
    public synchronized Boolean issueEventCoupon(User requestUser, Long eventCouponIdx) {
        User user = userRepository.findById(requestUser.getUserIdx()).orElseThrow(()-> new UserException(UserResponseStatus.INVALID_USER_ID));
        if (user.getUserCoupons() != null && user.getUserCoupons().stream().anyMatch(coupon -> coupon.getCoupon().getCouponIdx().equals(eventCouponIdx))) {
            return false;
        }
        Coupon couponEvent = couponRepository.findById(eventCouponIdx).orElseThrow(()-> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        if (couponEvent.getCouponQuantity() == 0) return false;
        UserCoupon coupon = UserCoupon.builder().user(user).coupon(couponEvent).couponUsed(false).build();
        // 갯수 갱신
        couponEvent.setCouponQuantity(couponEvent.getCouponQuantity()-1);
        couponRepository.save(couponEvent);
        // 발급된 쿠폰 DB에 저장
        userCouponRepository.save(coupon);
        // TODO: 알림 생성이 여기서 필요한지 논의

        String title = "이벤트 쿠폰 :"+ couponEvent.getCouponName();
        String content = couponEvent.getCouponDiscountRate()+"% 할인, 만료일: "+ couponEvent.getCouponValidDate();
        Notification notification = Notification.builder().title(title).content(content).notificationType(NotificationType.PERSONAL).cronExpression("").createdAt(LocalDateTime.now()).build();
        notificationRepository.save(notification);
        UserNotification userNotification = UserNotification.builder().notificationType(NotificationType.PERSONAL).user(user).createdAt(LocalDateTime.now()).title(title).content(content).template(notification).isRead(false).build();
        userNotificationRepository.save(userNotification);

        return true;
    }


    

    @Transactional
    public Boolean issueEventCoupon(User requestUser, Long eventCouponIdx) {
        User user = userRepository.findById(requestUser.getUserIdx())
                .orElseThrow(() -> new UserException(UserResponseStatus.INVALID_USER_ID));

        // 쿠폰 조회 (flush delay 유도)
        Coupon couponEvent = couponRepository.findById(eventCouponIdx)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));

        // 지연으로 동시성 충돌 유도
        try {
            Thread.sleep(300); // 시간 충분히 줘야 race 유도됨
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (couponEvent.getCouponQuantity() == 0) return false;

        // 쿠폰 수량 차감 및 저장
        int currentQuantity = couponEvent.getCouponQuantity();
        couponEvent.setCouponQuantity(currentQuantity - 1);

        // 쿠폰 저장 → flush 지연됨 (동시성 충돌 발생 유도)
        couponRepository.save(couponEvent);

        UserCoupon coupon = UserCoupon.builder()
                .user(user)
                .coupon(couponEvent)
                .couponUsed(false)
                .build();
        userCouponRepository.save(coupon);

        return true;
    }
    */

    public Boolean issueEventCoupon(User user, Long couponId) {
        String userSetKey   = "set.receive.couponId." + couponId;
        String stockHashKey = "hash.coupon.stock."  + couponId;
        String userIdx      = user.getUserIdx().toString();

        RLock lock = redissonClient.getLock("lock:coupon:" + couponId);
        boolean locked = false;
        try {
            // 최대 5초 대기, 성공 시 10초 후 자동 해제
            locked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("락 획득 실패");
            }

            // 1) 중복 체크
            if (!couponRedisRepository.sAdd(userSetKey, userIdx)) {
                throw new RuntimeException("쿠폰을 이미 발급 받았습니다.");
            }

            // 2) 재고 차감
            long remain = couponRedisRepository.hIncrBy(stockHashKey, "quantity", -1);
            if (remain < 0) {
                // 롤백
                couponRedisRepository.hIncrBy(stockHashKey, "quantity", +1);
                couponRedisRepository.sRem(userSetKey, userIdx);
                throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
            }

            // 3) 발급 로그 및 DB 저장
            couponRedisRepository.rPush(
                    "list.received.user",
                    objectMapper.writeValueAsString(user)
            );
            couponDBService.saveIssuedCouponToDB(user.getUserIdx(), couponId);

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 인터럽트", e);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화 실패", e);

        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    @Transactional
    public void createEvent(EventCouponCreateRequestDto request) {
        Product product = productRepository.findById(request.getProductIdx()).orElseThrow(()-> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        String[] dates = request.getExpiryDate().split("\\-");
        ZonedDateTime expiry = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2])).atStartOfDay().atZone(ZoneOffset.ofHours(9)); // +09:00
        Coupon coupon = Coupon.builder().couponName(request.getCouponName()).couponDiscountRate(request.getDiscount()).couponValidDate(Date.from(expiry.toInstant())).couponQuantity(request.getQuantity()).product(product).build();
        couponRepository.save(coupon);
    }

    @Transactional
    public void updateEvent(Long eventIdx, EventCouponCreateRequestDto request) {
        Coupon event = couponRepository.findById(eventIdx).orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        event.updateEvent(request);
        couponRepository.save(event);
    }

    @Transactional
    public void forceDeleteEvent(Long eventIdx) {
        Coupon coupon = couponRepository.findById(eventIdx).orElseThrow(()-> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        // 사용한 것만 빼고 전부 제거
        List<UserCoupon> issuedCoupons = coupon.getUserCoupons().stream().filter(userCoupon -> !userCoupon.getCouponUsed()).toList();
        userCouponRepository.deleteAll(issuedCoupons);
        couponRepository.delete(coupon);
    }



    private Boolean checkDuplicate(String key, Long userIdx) {
        return couponRedisRepository.sIsMember(key, ""+userIdx);
    }

    private Boolean checkQuantity(CouponRedisEntity couponRedisEntity, String key) {
        return couponRedisEntity.getCouponQuantity() <= couponRedisRepository.sCard(key);
    }

    private String JsonSerializer(User requestUser) throws JsonProcessingException {
        return objectMapper.writeValueAsString(requestUser);
    }

    /**
     * 남은 쿠폰 수량 조회
     */
    public Long getRemainingQuantity(Long couponId) {
        String stockHashKey = "hash.coupon.stock." + couponId;
        return couponRedisRepository.hGet(stockHashKey, "quantity");
    }

    /**
     * 발급받은 유저 수 조회
     */
    public long countIssuedUsers(Long couponId) {
        String userSetKey = "set.receive.couponId." + couponId;
        return couponRedisRepository.sCard(userSetKey);
    }

}
