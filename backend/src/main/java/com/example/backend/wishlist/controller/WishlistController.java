package com.example.backend.wishlist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "위시리스트 기능", description = "위시리스트 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    @Operation(summary = "위시리스트 추가/삭제", description = "상품을 위시리스트에 추가 또는 삭제합니다.")
    @PostMapping("/{productId}")
    public void toggleWishlist(@PathVariable Long productId) {
        // TODO: 구현
    }

    @Operation(summary = "위시리스트 조회", description = "회원의 위시리스트를 조회합니다.")
    @GetMapping("/list")
    public void getWishlist() {
        // TODO: 구현
    }
}
