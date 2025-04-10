package com.example.backend.user.model;

import com.example.backend.cart.model.Cart;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.order.model.Orders;
import com.example.backend.review.model.Review;
import com.example.backend.wishlist.model.Wishlist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private String userConfirmPassword;
    @Column(unique = true)
    private String userNickname;
    private String userPhone;
    private String userAddress;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isSocial;
    private Boolean enabled;
    private Boolean isAdmin;
    private Boolean likeEnabled;
    private Boolean newEnabled;
    private Boolean upgradeEnabled;
    private Boolean allowSms;
    private Boolean allowEmail;

    // review와 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    // Cart와 일대일 맵핑
    @OneToOne(mappedBy = "user")
    private Cart cart;

    // WishList와 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlists;

    // Order와 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<Orders> orders;

    // UserCoupon 과 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons;

    public void verify() {
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (this.isAdmin) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
            authorities.add(grantedAuthority);
        } else {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
            authorities.add(grantedAuthority);
        }

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

}
