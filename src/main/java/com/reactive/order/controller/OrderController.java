package com.reactive.order.controller;

import com.reactive.order.model.entity.request.OrderRequest;
import com.reactive.order.model.entity.response.ApiResponse;
import com.reactive.order.model.entity.response.OrderResponse;
import com.reactive.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> createOrder(
            @Valid @RequestBody OrderRequest request) {

        return orderService.createOrder(request)
                .map(order ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                        new ApiResponse<>(
                                                "Order created successfully",
                                                order
                                        )
                                )
                );
    }

    @GetMapping
    public Flux<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> getOrderById(
            @PathVariable String id) {

        return orderService.getOrderById(id)
                .map(order ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Order fetched successfully",
                                        order
                                )
                        )
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody OrderRequest request) {

        return orderService.updateOrder(id, request)
                .map(order ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Order updated successfully",
                                        order
                                )
                        )
                );
    }

    @PatchMapping("/{id}/cancel")
    public Mono<ResponseEntity<ApiResponse<Void>>> cancelOrder(
            @PathVariable String id) {

        return orderService.cancelOrder(id)
                .thenReturn(
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Order cancelled successfully",
                                        null
                                )
                        )
                );
    }

    @PatchMapping("/{id}/confirm")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> confirmOrder(
            @PathVariable String id) {

        return orderService.confirmOrder(id)
                .map(order ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Order confirmed successfully",
                                        order
                                )
                        )
                );
    }

    @PatchMapping("/{id}/payment-failed")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> markPaymentFailed(
            @PathVariable String id) {

        return orderService.markPaymentFailed(id)
                .map(order ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Order payment marked as failed",
                                        order
                                )
                        )
                );
    }
}