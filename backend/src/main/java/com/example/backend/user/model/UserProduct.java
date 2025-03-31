package com.example.backend.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usersProductIdx;

    @ManyToOne
    @JoinColumn(name="user_idx")
    private User user;

    // @ManyToOne
    // @JoinColumn(name="product_idx")
    // private Product products;
}
