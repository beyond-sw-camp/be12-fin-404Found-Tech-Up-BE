package com.example.backend.coupon.controller;

import com.example.backend.coupon.model.dto.request.AllCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.EventCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.request.EventCouponIssueRequestDto;
import com.example.backend.coupon.model.dto.response.CouponInfoDto;
import com.example.backend.coupon.model.dto.response.CouponListResponseDto;
import com.example.backend.coupon.model.dto.request.UserCouponCreateRequestDto;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.CouponResponseStatus;
import com.example.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "쿠폰 기능", description = "쿠폰 관련 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;

    @Operation( summary="이벤트 목록 조회", description="현재 진행 중인 이벤트 목록을 봅니다.")
    @GetMapping("/events")
    public BaseResponse<CouponListResponseDto> getEventList() {
        return new BaseResponseServiceImpl().getSuccessResponse(couponService.getEventList(), CouponResponseStatus.SUCCESS);
    }

    @Operation( summary= "이벤트 쿠폰 발급", description="이벤트에서 쿠폰을 발행하고 이벤트 쿠폰 재고를 차감합니다")
    @GetMapping("/events/{idx}")
    public BaseResponse<Object> issueEventCoupon(@AuthenticationPrincipal User user, @PathVariable Long idx) throws JsonProcessingException, InterruptedException {
        // 중복 발행 방지
        if (user == null) return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        Boolean result = couponService.issueEventCoupon(user,idx);
        if (!result) { // 쿠폰 재고 소진됨
            return new BaseResponseServiceImpl().getFailureResponse(CouponResponseStatus.OUT_OF_COUPON_STOCK);
        }
        return new BaseResponseServiceImpl().getSuccessResponse(CouponResponseStatus.SUCCESS);
    }

    @Operation(summary = "쿠폰 목록 조회", description = "전체 발급된 쿠폰 목록을 전부 조회합니다.")
    @GetMapping
    public BaseResponse<CouponListResponseDto> getCouponList() {
        CouponListResponseDto result = couponService.getCouponList();
        return new BaseResponseServiceImpl().getSuccessResponse(result, CouponResponseStatus.SUCCESS);
    }

    @Operation(summary = "쿠폰 목록 조회", description = "전체 발급된 쿠폰 목록을 페이지 번호에 따라 조회합니다.")
    @GetMapping("/{offset}")
    public BaseResponse<CouponListResponseDto> getCouponList(@PathVariable Integer offset) {
        if (offset == null) {
            offset = 0;
        }
        CouponListResponseDto result = couponService.getCouponPage(offset);
        return new BaseResponseServiceImpl().getSuccessResponse(result, CouponResponseStatus.SUCCESS);
    }

    @Operation(summary = "쿠폰 상세 조회", description = "특정한 idx의 쿠폰 상세 정보를 조회합니다.")
    @GetMapping("/details/{idx}")
    public BaseResponse<CouponInfoDto> getCouponList(@PathVariable Long idx) {
        CouponInfoDto result = couponService.getCouponInfo(idx);
        return new BaseResponseServiceImpl().getSuccessResponse(result, CouponResponseStatus.SUCCESS);
    }

    // ---------------- 관리자 ---------------------

    @Operation(summary="사용자별 쿠폰 발급", description="개별 사용자마다 수동 쿠폰 발급")
    @PostMapping("/issue")
    public BaseResponse<String> issueCoupon(@AuthenticationPrincipal User user, @RequestBody UserCouponCreateRequestDto request) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        Long couponIdx = couponService.CreateCouponForUser(request);
        log.info("issue coupon {}", couponIdx);
        // TODO: 사용자 알림 생성

        return new BaseResponseServiceImpl().getSuccessResponse(couponIdx.toString() + "번 쿠폰 발행 성공", CouponResponseStatus.SUCCESS);
    }

    @Operation(summary = "전체 쿠폰 발급", description = "전체에게 쿠폰 발급.")
    @PostMapping("/issueall")
    public BaseResponse<List<Long>> issueCouponsToAll(@AuthenticationPrincipal User user, @RequestBody AllCouponCreateRequestDto request) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        List<Long> coupons = couponService.CreateCouponForAll(request);
        log.info("issued {} coupons", coupons.size());
        return new BaseResponseServiceImpl().getSuccessResponse(coupons, CouponResponseStatus.SUCCESS);
    }


    @Operation(summary = "쿠폰 수정", description= "쿠폰 내용 수정")
    @PutMapping("/update/{idx}")
    public BaseResponse<String> updateCoupon(@AuthenticationPrincipal User user, @PathVariable Long idx, @RequestBody UserCouponCreateRequestDto request) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        couponService.updateCoupon(idx, request);
        return new BaseResponseServiceImpl().getSuccessResponse("수정 성공", CouponResponseStatus.SUCCESS);
    }

    @Operation(summary = "제한적 쿠폰 삭제", description= "단 한 명도 사용하지 않은 쿠폰 삭제")
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteCoupon(@AuthenticationPrincipal User user, @RequestParam Long idx) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        Boolean result = couponService.deleteCoupon(idx);
        if (result) {
            return new BaseResponseServiceImpl().getSuccessResponse("성공", CouponResponseStatus.SUCCESS);
        } else {
            return new BaseResponseServiceImpl().getFailureResponse(CouponResponseStatus.CANNOT_DELETE_COUPON);
        }
    }

    @Operation(summary = "쿠폰 검색", description = "키워드가 포함된 쿠폰 제목에 따라 쿠폰을 검색합니다.")
    @GetMapping("/search")
    public BaseResponse<CouponListResponseDto> searchCoupon(@AuthenticationPrincipal User user, @RequestParam String keyword) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        if (keyword == null) {
            keyword = "";
        }
        CouponListResponseDto result = couponService.searchCoupon(keyword);
        return new BaseResponseServiceImpl().getSuccessResponse(result, CouponResponseStatus.SUCCESS);
    }


    @Operation(summary = "선착순 쿠폰 발급 이벤트 등록", description = "선착순 쿠폰 발급 이벤트를 등록합니다")
    @PostMapping("/events")
    public BaseResponse<Object> registerEvents(@AuthenticationPrincipal User user, @RequestBody EventCouponCreateRequestDto request) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        couponService.createEvent(request);
        return new BaseResponseServiceImpl().getSuccessResponse(CouponResponseStatus.SUCCESS);
    }

    @Operation( summary= "선착순 쿠폰 발급 이벤트 수정" , description="예약된 이벤트를 수정합니다")
    @PutMapping("/events/{eventIdx}")
    public BaseResponse<Object> updateEvents(@AuthenticationPrincipal User user, @RequestBody EventCouponCreateRequestDto request, @PathVariable Long eventIdx) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        couponService.updateEvent(eventIdx, request);
        return new BaseResponseServiceImpl().getSuccessResponse(CouponResponseStatus.SUCCESS);
    }

    @Operation( summary= "선착순 쿠폰 발급 이벤트 삭제" , description="발행한 이벤트를 삭제합니다. 이 쿠폰은 사용한 것은 삭제되지 않으므로 주의해야 합니다.")
    @DeleteMapping("/events")
    public BaseResponse<Object> deleteEvents(@AuthenticationPrincipal User user, @RequestParam Long idx) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        couponService.forceDeleteEvent(idx);
        return new BaseResponseServiceImpl().getSuccessResponse(CouponResponseStatus.SUCCESS);
    }

}
