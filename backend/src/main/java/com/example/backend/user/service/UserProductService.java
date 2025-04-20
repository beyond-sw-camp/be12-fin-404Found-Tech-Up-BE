package com.example.backend.user.service;

import com.example.backend.global.exception.ProductException;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.product.model.dto.ProductResponseDto;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.model.UserProduct;
import com.example.backend.product.model.Product;
import com.example.backend.user.model.dto.response.MyProductResponseDto;
import com.example.backend.user.repository.UserProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserProductService {
    private final UserProductRepository userProductRepository;
    private final ProductRepository productRepository;

    public void register(Long productIdx, User user) {
        Product product = productRepository.findById(productIdx)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        // 이미 등록된 제품인지 확인 (중복 등록 방지)
        boolean exists = userProductRepository.existsByUserAndProducts(user, product);
        if (exists) {
            throw new UserException(UserResponseStatus.USER_PRODUCT_IN_USE);
        }

        UserProduct userProduct = UserProduct.builder()
                .user(user)
                .products(product)
                .build();

        userProductRepository.save(userProduct);
    }

    public MyProductResponseDto myProduct(User user) {
        List<UserProduct> userProducts = userProductRepository.findByUser(user);

        List<ProductResponseDto> productDtos = userProducts.stream()
                .map(userProduct -> {
                    Product product = userProduct.getProducts();
                    return ProductResponseDto.from(product);
                })
                .collect(Collectors.toList());

        return MyProductResponseDto.builder()
                .userIdx(user.getUserIdx())
                .products(productDtos)
                .build();
    }

    public void delete(Long productIdx, User user) {
        Product product = productRepository.findById(productIdx)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        UserProduct userProduct = userProductRepository.findByUserAndProducts(user, product)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_PRODUCT_FAIL));

        userProductRepository.delete(userProduct);
    }
}
