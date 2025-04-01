package com.example.backend.review.model;

import com.example.backend.product.model.Product;
import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;
    private int reviewRating;
    private String reviewContent;
    private String reviewImg;
    private Date reviewDate;

    // 유저와 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;

}
