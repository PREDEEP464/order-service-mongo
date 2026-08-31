package com.reactive.order.service;

import com.reactive.order.model.entity.request.OrderRequest;
import com.reactive.order.model.entity.response.OrderResponse;
import com.reactive.order.model.entity.response.OrderPaymentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<OrderResponse> createOrder(OrderRequest request);

    Flux<OrderResponse> getAllOrders();

    Mono<OrderResponse> getOrderById(String id);

    Mono<OrderResponse> cancelOrder(String id);

    Mono<OrderResponse> confirmOrder(String id);

    Mono<OrderResponse> markPaymentFailed(String id);

    Mono<OrderPaymentResponse> getOrderPaymentDetails(String id);
}