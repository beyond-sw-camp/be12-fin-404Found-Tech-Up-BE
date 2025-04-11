package com.example.backend.order.controller;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.OrderResponseStatus;
import com.example.backend.order.model.Orders;
import com.example.backend.order.model.dto.OrderCancelResponseDto;
import com.example.backend.order.model.dto.OrderResponseDto;
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
    @PostMapping
    public BaseResponse<OrderResponseDto> placeOrder(
            @AuthenticationPrincipal User loginUser
    ) {
        Orders order = orderService.placeOrder(loginUser);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "상품 결제", description = "주문한 상품을 결제합니다. 결제 식별자(paymentId)를 사용해 PortOne API 로 결제 금액을 검증합니다.")
    @PostMapping("/payment/{orderId}")
    public BaseResponse<OrderResponseDto> payOrder(
            @AuthenticationPrincipal User loginUser,
            @RequestParam @PathVariable Long orderId
    ) {
        Orders order = orderService.payOrder(loginUser, orderId);
        OrderResponseDto response = OrderResponseDto.from(order);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }


    @Operation(summary = "주문 취소", description = "회원의 주문을 취소합니다.")
    @PostMapping("/cancel/{orderId}")
    public BaseResponse<OrderCancelResponseDto> cancelOrder(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        OrderCancelResponseDto response = orderService.cancelOrder(loginUser, orderId);
        return baseResponseService.getSuccessResponse(response, OrderResponseStatus.SUCCESS);
    }

    @Operation(summary = "주문 내역 조회", description = "회원의 주문 내역을 조회합니다.")
    @GetMapping("/history")
    public BaseResponse<List<OrderResponseDto>> getOrderHistory(
            @AuthenticationPrincipal User loginUser
    ) {
        List<Orders> orders = orderService.getOrderHistory(loginUser);
        List<OrderResponseDto> responses = orders.stream()
                .map(OrderResponseDto::from)
                .collect(Collectors.toList());
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
}
