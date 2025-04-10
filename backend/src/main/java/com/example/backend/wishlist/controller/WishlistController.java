package com.example.backend.wishlist.controller;

import com.example.backend.user.model.User;
import com.example.backend.wishlist.dto.WishlistResponseDto;
import com.example.backend.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "위시리스트 기능", description = "위시리스트 기능 API")
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(
            summary = "위시리스트 토글 (추가/삭제)",
            description = "로그인한 사용자가 특정 상품(productIdx)을 위시리스트에 추가하거나, 이미 등록되어 있으면 삭제합니다. (토글 방식)"
    )
    @PostMapping("/toggle/{productIdx}")
    public ResponseEntity<String> toggleWishlist(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser,
            @Parameter(description = "상품 고유번호", required = true)
            @PathVariable Long productIdx
    ) {
        String resultMessage = wishlistService.toggleWishlist(loginUser, productIdx);
        return ResponseEntity.ok(resultMessage);
    }

    @Operation(
            summary = "위시리스트 조회",
            description = "로그인한 사용자가 본인의 위시리스트에 등록된 모든 상품 정보를 조회합니다."
    )
    @GetMapping("")
    public ResponseEntity<List<WishlistResponseDto>> getWishlist(
            @Parameter(description = "로그인한 사용자 정보", required = true)
            @AuthenticationPrincipal User loginUser
    ) {
        List<WishlistResponseDto> response = wishlistService.getWishlist(loginUser);
        return ResponseEntity.ok(response);
    }
}
