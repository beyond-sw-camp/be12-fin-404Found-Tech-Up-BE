package com.example.backend.wishlist.repository;

import com.example.backend.wishlist.model.Wishlist;
import com.example.backend.user.model.User;
import com.example.backend.product.model.Product;
import com.example.backend.admin.model.TopWishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    List<Wishlist> findByUser(User user);
  
    @Query("SELECT w.product.productIdx productIdx, w.product.name productName, w.product.brand brand, w.product.price price, w.product.discount discount, count(w.wishlistIdx) cw FROM Wishlist w GROUP BY w.product ORDER by cw DESC")
    List<TopWishList> countWishlistGroupByProduct();

    // 알림 발행을 위해, 이 제품에 위시리스트 추가한 사람들 가져오기
    @Query("SELECT w.user.userIdx FROM Wishlist w WHERE w.product.productIdx = :productId")
    List<Long> findUserIdxByProductIdx(Long productId);
}
