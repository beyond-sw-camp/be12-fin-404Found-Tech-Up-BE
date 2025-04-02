package com.example.backend.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "주문 API", description = "상품 주문 및 결제 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Operation(summary = "상품 주문", description = "장바구니에서 선택한 상품을 주문합니다.")
    @PostMapping
    public void placeOrder() {
        // TODO: 구현
    }

    @Operation(summary = "상품 결제", description = "주문한 상품을 결제합니다.")
    @PostMapping("/payment")
    public void payOrder() {
        // TODO: 구현
    }

    @Operation(summary = "주문 취소", description = "회원이 주문을 취소합니다.")
    @PostMapping("/cancel")
    public void cancelOrder() {
        // TODO: 구현
    }

    @Operation(summary = "주문 내역 조회", description = "회원의 주문 내역을 조회합니다.")
    @GetMapping
    public void getOrderHistory() {
        // TODO: 구현
    }

    @Operation(summary = "주문 상세 조회", description = "회원의 주문 내역 상세를 조회합니다.")
    @GetMapping("/{orderId}")
    public void getOrderDetail(@PathVariable Long orderId) {
        // TODO: 구현
    }

    @Operation(summary = "환불 요청", description = "회원이 환불을 요청합니다.")
    @PostMapping("/refund")
    public void requestRefund() {
        // TODO: 구현
    }
}
