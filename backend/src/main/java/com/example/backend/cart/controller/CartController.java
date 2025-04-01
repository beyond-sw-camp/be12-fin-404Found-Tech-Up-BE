package com.example.backend.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    @Operation(summary = "장바구니 담기", description = "상품을 장바구니에 추가합니다.")
    @PostMapping("/add")
    public void addToCart() {
        // TODO: 구현
    }

    @Operation(summary = "장바구니 삭제", description = "장바구니에서 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public void removeFromCart(@PathVariable Long productId) {
        // TODO: 구현
    }

    @Operation(summary = "장바구니 조회", description = "장바구니에 담긴 상품들을 조회합니다.")
    @GetMapping("/list")
    public void getCartItems() {
        // TODO: 구현
    }
}
