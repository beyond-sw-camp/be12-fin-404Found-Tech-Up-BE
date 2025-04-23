package com.example.backend.order.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderVerifyRequestDto {
    private String paymentId;
    private Long couponIdx;
}
