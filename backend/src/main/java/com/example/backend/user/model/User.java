package com.example.backend.user.model;

import com.example.backend.cart.model.Cart;
import com.example.backend.order.model.Order;
import com.example.backend.wishlist.model.Wishlist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;
    private String userEmail;
    private String userPassword;
    private String userNickname;
    private String userPhone;
    private String userAddress;
    private Boolean isActive;
    private Date createdAt;
    private Boolean isSocial;
    private Boolean enabled;
    private Boolean isAdmin;
    private Boolean likeEnabled;
    private Boolean newEnabled;
    private Boolean upgradeEnabled;

    // Cart와 일대일 맵핑
    @OneToOne(mappedBy = "user")
    private Cart cart;

    // WishList와 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlists;

    // Order와 일대다 맵핑
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.isAdmin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
