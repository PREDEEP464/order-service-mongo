package com.reactive.order.dao.api;

import com.reactive.order.model.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findByProductId(Long productId);

    Flux<Order> findByStatus(String status);
}