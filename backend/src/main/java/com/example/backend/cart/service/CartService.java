package com.example.backend.cart.service;

import com.example.backend.cart.model.Cart;
import com.example.backend.cart.model.CartItem;
import com.example.backend.cart.model.dto.CartItemResponseDto;
import com.example.backend.cart.model.dto.CartItemRequestDto;
import com.example.backend.cart.repository.CartRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // 로그인한 사용자의 장바구니 목록 조회
    public List<CartItemResponseDto> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .collect(Collectors.toList());
    }

    // 장바구니에 상품 추가
    // CartItemRequestDto의 'idx' 필드를 상품 수량으로 사용
    public void addToCart(User user, Long productId, CartItemRequestDto dto) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        int quantityToAdd = dto.getIdx();

        // 이미 해당 상품이 장바구니에 있는지 확인
        Optional<CartItem> optionalItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductIdx().equals(productId))
                .findFirst();
        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            // CartItem에 수량 변경을 위한 setter가 없다면, 직접 추가할 수 있도록 setter 또는 update 메서드를 추가하세요.
            item.setCartItemQuantity(item.getCartItemQuantity() + quantityToAdd);
        } else {
            CartItem newItem = CartItem.builder()
                    .cartItemQuantity(quantityToAdd)
                    .product(product)
                    .cart(cart)
                    .build();
            cart.getCartItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    // 장바구니 항목 삭제
    public void removeCartItem(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().removeIf(item -> item.getCartItemIdx().equals(cartItemId));
        cartRepository.save(cart);
    }

    // 로그인한 사용자의 Cart가 없으면 생성하는 헬퍼 메서드
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }
}
