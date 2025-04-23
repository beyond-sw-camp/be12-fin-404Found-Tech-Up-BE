package com.example.backend.order.controller;

import com.example.backend.global.exception.BaseException;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.responseStatus.OrderResponseStatus;
import com.example.backend.order.model.Orders;
import com.example.backend.order.model.dto.OrderCancelResponseDto;
import com.example.backend.order.model.dto.OrderRequestDto;
import com.example.backend.order.model.dto.OrderResponseDto;
import com.example.backend.order.model.dto.OrderVerifyRequestDto;
import com.example.backend.order.service.OrderService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "주문 API", description = "상품 주문 및 결제 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final BaseResponseService baseResponseService;

    @Operation(summary = "상품 주문", description = "장바구니에서 선택한 상품을 주문합니다.")
    @PostMapping("")
    public BaseResponse<OrderResponseDto> placeOrder(
            @AuthenticationPrincipal User loginUser,
            @RequestBody OrderRequestDto dto
    ) {
        Orders order = orderService.placeOrder(loginUser, dto);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "결제 검증", description = "결제 내역을 검증합니다. 결제 식별자(paymentId)를 사용해 PortOne API 로 결제 금액을 검증합니다.")
    @PostMapping("/verify/{orderIdx}")
    public BaseResponse<OrderResponseDto> payOrder(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderIdx,
            @RequestBody OrderVerifyRequestDto dto
    ) {
        Orders order = orderService.verify(loginUser, orderIdx, dto);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "주문 내역 조회", description = "회원의 주문 내역을 조회합니다.")
    @GetMapping("/history")
    public BaseResponse<List<OrderResponseDto>> getOrderHistory(
            @AuthenticationPrincipal User loginUser
    ) {
        List<OrderResponseDto> responses = orderService.getOrderHistory(loginUser);
        return baseResponseService.getSuccessResponse(responses, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "주문 상세 조회", description = "회원의 주문 내역 상세를 조회합니다.")
    @GetMapping("/{orderId}")
    public BaseResponse<OrderResponseDto> getOrderDetail(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        Orders order = orderService.getOrderDetail(loginUser, orderId);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "환불 요청", description = "회원이 환불을 요청합니다.")
    @PostMapping("/refund/{orderId}")
    public BaseResponse<OrderCancelResponseDto> requestRefund(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        OrderCancelResponseDto response = orderService.requestRefund(loginUser, orderId);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    // ------------------- 여기서부터 관리자 기능 ---------------------

    @Operation(
            summary     = "주문 취소",
            description = "회원의 주문을 취소합니다. 이미 결제된 경우 재고를 복원하고 환불을 요청합니다."
    )
    @PostMapping("/cancel/{orderId}")
    public BaseResponse<OrderResponseDto> cancelOrder(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        Orders order = orderService.cancelOrder(loginUser, orderId);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary="관리자의 사용자 주문 내역 조회", description="관리자가 사용자 주문 내역들을 볼 때 사용합니다.")
    @GetMapping("/orderlist/{idx}")
    public BaseResponse<List<OrderResponseDto>> getOrderList(@AuthenticationPrincipal User user, @PathVariable Long idx) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        if (idx == null) {
            throw new BaseException(CommonResponseStatus.BAD_REQUEST);
        }
        List<OrderResponseDto> result = orderService.getOrdersByUserId(idx);
        return baseResponseService.getSuccessResponse(result, OrderResponseStatus.SUCCESS);
    }
}
