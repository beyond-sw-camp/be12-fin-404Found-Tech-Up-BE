package com.example.backend.user.model;

import com.example.backend.board.model.Board;
import com.example.backend.cart.model.Cart;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.user.model.UserProduct;
import com.example.backend.order.model.Orders;
import com.example.backend.order.model.ShippingAddress;
import com.example.backend.review.model.Review;
import com.example.backend.wishlist.model.Wishlist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;
    @Column(unique = true)
    private String userEmail;
    private String userPassword;
    @Column(unique = true)
    private String userNickname;
    private String userPhone;
    private String userAddress;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String isSocial;
    private String kakaoId;
    private Boolean enabled;
    private Boolean isAdmin;
    private Boolean likeEnabled;
    private Boolean newEnabled;
    private Boolean upgradeEnabled;
    private Boolean allowSms;
    private Boolean allowEmail;

    private Boolean alarmEnabled;

    // OAuth2 속성을 저장하기 위한 필드
    @Transient
    private Map<String, Object> oauth2Attributes;

    // review와 일대다 맵핑
    @BatchSize(size=10)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // Cart와 일대일 맵핑
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    // WishList와 일대다 맵핑
    @BatchSize(size=10)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;

    // Order와 일대다 맵핑
    @BatchSize(size=100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Orders> orders;

    @BatchSize(size=100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Board> posts;

    // Order와 일대다 맵핑
    @BatchSize(size=10)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ShippingAddress> shippingAddresses;

    // UserCoupon 과 일대다 맵핑
    @BatchSize(size=100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons;

    // UserCoupon 과 일대다 맵핑
    @BatchSize(size=100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserProduct> userProducts;

    // User.java
    public static User of(String email) {
        return User.builder()
                .userEmail(email)
                .build();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // OAuth2 속성 설정 메서드
    public void setOAuth2Attributes(Map<String, Object> attributes) {
        this.oauth2Attributes = attributes;
    }

    // 인증 활성화
    public void verify() {
        this.enabled = true;
    }
}
