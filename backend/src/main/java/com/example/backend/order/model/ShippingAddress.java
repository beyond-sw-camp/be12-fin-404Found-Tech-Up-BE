package com.example.backend.order.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 수신인
    private String recipientName;
    // 주소
    private String addressLine1;
    // 주소 상세
    private String addressLine2;
    // 우편번호
    private String postalCode;
    // 전화번호
    private String phone;
    // 이메일
    private String email;
    // 요청사항
    private String memo;

    // 유저와 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_idx")
    private User user;
}
