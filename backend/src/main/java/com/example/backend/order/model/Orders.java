package com.example.backend.order.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderIdx;
    private double orderTotalPrice;
    private String orderStatus;
    private Date orderDate;

    // 유저와 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;
    // 주문상세정보와 일대다 맵핑
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
