package com.example.backend.wishlist.service;

import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.wishlist.model.dto.WishlistResponseDto;
import com.example.backend.wishlist.model.Wishlist;
import com.example.backend.wishlist.model.dto.WishlistToggleResponseDto;
import com.example.backend.wishlist.repository.WishlistRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    /**
     * 토글 방식으로 위시리스트 처리.
     * - 해당 상품이 이미 위시리스트에 있으면 삭제하고 "삭제" 메시지를,
     * - 없으면 추가하고 "추가" 메시지를 반환.
     */
    public WishlistToggleResponseDto toggleWishlist(User user, Long productIdx) {
        Product product = productRepository.findById(productIdx)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        // 이미 위시리스트에 존재하는지 확인
        return wishlistRepository.findByUserAndProduct(user, product)
                .map(existingWishlist -> {
                    wishlistRepository.delete(existingWishlist);
                    return WishlistToggleResponseDto.from(productIdx, "위시리스트에서 상품을 삭제했습니다.");
                })
                .orElseGet(() -> {
                    Wishlist wishlist = Wishlist.builder()
                            .user(user)
                            .product(product)
                            .build();
                    wishlistRepository.save(wishlist);
                    return WishlistToggleResponseDto.from(productIdx, "상품을 위시리스트에 추가했습니다.");
                });
    }

    /**
     * 로그인한 사용자의 위시리스트 항목을 조회하여 DTO 리스트로 반환.
     */
    public List<WishlistResponseDto> getWishlist(User user) {
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
        return wishlists.stream()
                .map(WishlistResponseDto::from)
                .collect(Collectors.toList());
    }
}
