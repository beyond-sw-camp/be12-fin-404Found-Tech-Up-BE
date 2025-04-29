package com.example.backend.user.repository;

import com.example.backend.product.model.Product;
import com.example.backend.user.model.User;
import com.example.backend.user.model.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    boolean existsByUserAndProducts(User user, Product product);

    List<UserProduct> findByUser(User user);

    Optional<UserProduct> findByUserAndProducts(User user, Product product);
}
