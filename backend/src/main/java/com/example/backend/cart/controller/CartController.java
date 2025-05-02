package com.example.backend.cart.controller;

import com.example.backend.cart.model.dto.CartItemUpdateResponseDto;
import com.example.backend.cart.model.dto.CartItemRequestDto;
import com.example.backend.cart.model.dto.CartItemResponseDto;
import com.example.backend.cart.model.dto.CartItemUpdateRequestDto;
import com.example.backend.cart.service.CartService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CartResponseStatus;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "장바구니 기능", description = "장바구니 기능 API")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final BaseResponseService baseResponseService;

    @Operation(
            summary = "장바구니 조회",
            description = "로그인한 사용자의 장바구니 내역(카트 아이템 목록 및 전체 가격)을 조회합니다."
    )
    // 그냥 카트로 요청시, 바로 장바구니 목록 반환
    @GetMapping("")
    public BaseResponse<List<CartItemResponseDto>> getCart(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser
    ) {
        if (loginUser == null) {
            return baseResponseService.getFailureResponse(CartResponseStatus.USER_NOT_LOGGED);
        }
        List<CartItemResponseDto> response = cartService.getCartItems(loginUser);
        return baseResponseService.getSuccessResponse(response, CartResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "장바구니에 상품 추가",
            description = "로그인한 사용자가 특정 상품을 장바구니에 추가합니다. URL 경로로 productIdx를 받고, 요청 본문에 수량 정보를 전달합니다."
    )
    @PostMapping("/add/{productIdx}")
    public BaseResponse<CartItemResponseDto> add(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "추가할 상품의 고유번호", required = true)
            @PathVariable Long productIdx,
            @Parameter(description = "장바구니에 추가할 상품 정보 DTO (수량은 cartItemQuantity 필드)", required = true)
            @RequestBody CartItemRequestDto cartItemRequestDto) {
        if (loginUser == null) {
            return baseResponseService.getFailureResponse(CartResponseStatus.USER_NOT_LOGGED);
        }
        CartItemResponseDto response = cartService.addToCart(loginUser, productIdx, cartItemRequestDto);
        return baseResponseService.getSuccessResponse(response, CartResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "장바구니 항목 삭제",
            description = "회원이 장바구니에 담긴 특정 상품 항목을 삭제합니다. 해당 항목의 ID(cartItemIdx)를 통해 삭제를 처리합니다."
    )
    @DeleteMapping("/delete/{cartItemIdx}")
    public BaseResponse<CartItemUpdateResponseDto> delete(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "삭제할 카트 아이템 고유번호", required = true)
            @PathVariable Long cartItemIdx) {
        if (loginUser == null) {
            return baseResponseService.getFailureResponse(CartResponseStatus.USER_NOT_LOGGED);
        }
        CartItemUpdateResponseDto response = cartService.removeCartItem(loginUser, cartItemIdx);
        return baseResponseService.getSuccessResponse(response, CartResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "장바구니 항목 비우기",
            description = "회원이 장바구니에 담긴 모든 상품 항목을 삭제합니다."
    )
    @DeleteMapping("/clear")
    public BaseResponse<CartItemUpdateResponseDto> clear(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser) {
        if (loginUser == null) {
            return baseResponseService.getFailureResponse(CartResponseStatus.USER_NOT_LOGGED);
        }
        CartItemUpdateResponseDto response = cartService.clearCart(loginUser);
        return baseResponseService.getSuccessResponse(response, CartResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "장바구니 항목 수량 변경",
            description = "특정 상품의 장바구니 항목 수량을 증가 또는 감소시킵니다. 최종 수량이 0 이하이면 해당 항목은 삭제됩니다."
    )
    @PutMapping("/update/{productIdx}")
    public BaseResponse<CartItemUpdateResponseDto> updateCartItemQuantity(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "수량 변경할 상품의 고유번호", required = true)
            @PathVariable Long productIdx,
            @Parameter(description = "수량 변경 요청 DTO (양수면 추가, 음수면 차감)", required = true)
            @RequestBody CartItemUpdateRequestDto updateDto) {
        if (loginUser == null) {
            return baseResponseService.getFailureResponse(CartResponseStatus.USER_NOT_LOGGED);
        }
        CartItemUpdateResponseDto response = cartService.updateCartItemQuantity(loginUser, productIdx, updateDto.getDeltaQuantity());
        return baseResponseService.getSuccessResponse(response, CartResponseStatus.SUCCESS);
    }
}
