package com.reactive.order.model.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentResponse {

    private String orderId;
    private String customerName;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentId;
    private BigDecimal paymentAmount;
    private String paymentStatus;
}