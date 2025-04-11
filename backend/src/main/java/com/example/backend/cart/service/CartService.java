package com.example.backend.cart.service;

import com.example.backend.cart.model.Cart;
import com.example.backend.cart.model.CartItem;
import com.example.backend.cart.model.dto.CartItemUpdateResponseDto;
import com.example.backend.cart.model.dto.CartItemResponseDto;
import com.example.backend.cart.model.dto.CartItemRequestDto;
import com.example.backend.cart.repository.CartRepository;
import com.example.backend.global.exception.CartException;
import com.example.backend.global.exception.ProductException;
import com.example.backend.global.response.responseStatus.CartResponseStatus;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
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
    public CartItemResponseDto addToCart(User user, Long productId, CartItemRequestDto dto) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        int quantityToAdd = dto.getCartItemQuantity();

        // 이미 해당 상품이 장바구니에 있는 경우
        Optional<CartItem> optionalItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductIdx().equals(productId))
                .findFirst();

        CartItem targetItem;
        if (optionalItem.isPresent()) {
            targetItem = optionalItem.get();
            targetItem.setCartItemQuantity(targetItem.getCartItemQuantity() + quantityToAdd);
        } else {
            targetItem = CartItem.builder()
                    .cartItemQuantity(quantityToAdd)
                    .product(product)
                    .cart(cart)
                    .build();
            cart.getCartItems().add(targetItem);
        }
        cartRepository.save(cart);
        return CartItemResponseDto.from(targetItem);
    }

    // 장바구니 항목 삭제
    public CartItemUpdateResponseDto removeCartItem(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        Optional<CartItem> optionalItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemIdx().equals(cartItemId))
                .findFirst();
        if (optionalItem.isEmpty()) {
            throw new CartException(CartResponseStatus.CART_ITEM_DELETE_FAIL);
        }
        cart.getCartItems().removeIf(item -> item.getCartItemIdx().equals(cartItemId));
        cartRepository.save(cart);
        return CartItemUpdateResponseDto.from(cartItemId);
    }

    // 장바구니 항목 수량 업데이트
    public CartItemUpdateResponseDto updateCartItemQuantity(User user, Long productId, int deltaQuantity) {
        Cart cart = getOrCreateCart(user);
        Optional<CartItem> optionalItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductIdx().equals(productId))
                .findFirst();
        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            int newQuantity = item.getCartItemQuantity() + deltaQuantity;
            if (newQuantity > 0) {
                item.setCartItemQuantity(newQuantity);
            } else {
                cart.getCartItems().remove(item);
            }
            cartRepository.save(cart);
            // 반환하는 DTO의 cartItemIdx는 수정되었거나 삭제된 항목의 idx
            return CartItemUpdateResponseDto.from(item.getCartItemIdx());
        } else {
            // 만약 수정할 항목이 없고, deltaQuantity가 양수이면 신규 추가
            if (deltaQuantity > 0) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
                CartItem newItem = CartItem.builder()
                        .cartItemQuantity(deltaQuantity)
                        .product(product)
                        .cart(cart)
                        .build();
                cart.getCartItems().add(newItem);
                cartRepository.save(cart);
                return CartItemUpdateResponseDto.from(newItem.getCartItemIdx());
            } else {
                throw new CartException(CartResponseStatus.CART_ITEM_UPDATE_FAIL);
            }
        }
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
