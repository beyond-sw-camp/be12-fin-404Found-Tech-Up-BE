package com.example.backend.wishlist.repository;

import com.example.backend.wishlist.model.Wishlist;
import com.example.backend.user.model.User;
import com.example.backend.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    List<Wishlist> findByUser(User user);
}
