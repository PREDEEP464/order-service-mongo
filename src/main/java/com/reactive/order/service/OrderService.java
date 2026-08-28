package com.reactive.order.service;

import com.reactive.order.model.entity.request.OrderRequest;
import com.reactive.order.model.entity.response.OrderResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<OrderResponse> createOrder(OrderRequest request);

    Flux<OrderResponse> getAllOrders();

    Mono<OrderResponse> getOrderById(String id);

    Mono<OrderResponse> updateOrder(String id, OrderRequest request);

    Mono<Void> cancelOrder(String id);

    Mono<OrderResponse> confirmOrder(String id);

    Mono<OrderResponse> markPaymentFailed(String id);
}