package com.example.backend.coupon.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventCouponCreateRequest {
    private Integer quantity;
    private String category;
    private Integer discount;
    private String expiryDate;
}
