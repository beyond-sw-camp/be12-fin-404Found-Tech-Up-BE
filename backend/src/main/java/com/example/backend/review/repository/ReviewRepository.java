package com.example.backend.review.repository;

import com.example.backend.product.model.Product;
import com.example.backend.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProductOrderByReviewDateDesc(Product product);

    List<Review> findByProduct(Product p);
}