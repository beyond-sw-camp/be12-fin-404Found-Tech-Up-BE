package com.example.backend.cart.repository;

import com.example.backend.cart.model.Cart;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
