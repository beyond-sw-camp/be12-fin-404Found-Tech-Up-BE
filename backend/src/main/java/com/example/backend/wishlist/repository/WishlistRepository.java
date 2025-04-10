package com.example.backend.wishlist.repository;

import com.example.backend.wishlist.model.Wishlist;
import com.example.backend.user.model.User;
import com.example.backend.product.model.Product;
import com.example.backend.admin.model.TopWishListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    List<Wishlist> findByUser(User user);
  
    @Query("SELECT w.product.brand brand, count(w.wishlistIdx) cw FROM Wishlist w GROUP BY w.product ORDER by cw DESC")
    List<TopWishListDto> countWishlistGroupByProduct();

}
