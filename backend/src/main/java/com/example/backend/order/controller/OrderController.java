package com.example.backend.order.controller;

import com.example.backend.order.model.Orders;
import com.example.backend.order.model.dto.OrderResponseDto;
import com.example.backend.order.service.OrderService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "상품 주문", description = "장바구니에서 선택한 상품을 주문합니다.")
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @AuthenticationPrincipal User loginUser
    ) {
        Orders order = orderService.placeOrder(loginUser);
        OrderResponseDto response = OrderResponseDto.from(order);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 결제", description = "주문한 상품을 결제합니다.")
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<OrderResponseDto> payOrder(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        Orders order = orderService.payOrder(loginUser, orderId);
        OrderResponseDto response = OrderResponseDto.from(order);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주문 취소", description = "회원의 주문을 취소합니다.")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        orderService.cancelOrder(loginUser, orderId);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }

    @Operation(summary = "주문 내역 조회", description = "회원의 주문 내역을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDto>> getOrderHistory(
            @AuthenticationPrincipal User loginUser
    ) {
        List<Orders> orders = orderService.getOrderHistory(loginUser);
        List<OrderResponseDto> responses = orders.stream()
                .map(OrderResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "주문 상세 조회", description = "회원의 주문 내역 상세를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetail(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        Orders order = orderService.getOrderDetail(loginUser, orderId);
        OrderResponseDto response = OrderResponseDto.from(order);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "환불 요청", description = "회원이 환불을 요청합니다.")
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<String> requestRefund(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long orderId
    ) {
        orderService.requestRefund(loginUser, orderId);
        return ResponseEntity.ok("환불 요청이 접수되었습니다.");
    }
}
