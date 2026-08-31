package com.reactive.order.model.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;

    private String orderId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}