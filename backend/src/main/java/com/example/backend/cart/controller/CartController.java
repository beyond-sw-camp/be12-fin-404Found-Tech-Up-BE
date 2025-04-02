package com.example.backend.cart.controller;

import com.example.backend.cart.model.Cart;
import com.example.backend.cart.model.dto.CartItemRequestDto;
import com.example.backend.cart.service.CartService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 기능", description = "장바구니 기능 API")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 조회",
            description = "로그인한 사용자의 장바구니 내역(카트 아이템 목록 및 전체 가격)을 조회한다."
    )
    // 그냥 카트로 요청시, 바로 장바구니 목록 반환
    @GetMapping("")
    public ResponseEntity getCart(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser
    ) {
        return null;
    }

    @Operation(
            summary = "장바구니에 상품 추가",
            description = "로그인한 사용자가 특정 상품을 장바구니에 추가합니다. URL 경로로 productIdx를 받고, 추가 옵션 및 수량 정보는 요청 본문(CartItemRequestDto)에서 전달됩니다."
    )
    @PostMapping("/add/{productIdx}")
    public ResponseEntity add(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "추가할 상품의 고유번호", required = true)
            @PathVariable Long productIdx,
            @Parameter(description = "장바구니에 추가할 상품 정보 DTO", required = true)
            @RequestBody CartItemRequestDto cartItemRequestDto) {
        return null;
    }

    @Operation(
            summary = "장바구니 항목 삭제",
            description = "회원이 장바구니에 담긴 특정 상품 항목을 삭제합니다. 해당 항목의 ID(cartItemId)를 통해 삭제를 처리합니다."
    )
    @DeleteMapping("/delete/{cartItemIdx}")
    public ResponseEntity delete(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "삭제할 카트 아이템 고유번호", required = true)
            @PathVariable Long cartItemIdx) {
        return null;
    }
}
