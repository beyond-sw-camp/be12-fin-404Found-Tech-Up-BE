// OrderRequestDto.java
package com.example.backend.order.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private String recipientName;
    private String address;
    private String postalCode;
    private String addressDetail;
    private String phone;
    private String email;
    private String memo;
    private Long couponIdx;
    private double shipCost;
    private String paymentMethod;
    private String shippingMethod;
    private List<OrderDetailRequestDto> items;
}
