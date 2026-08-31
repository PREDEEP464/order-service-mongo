package com.reactive.order.exception;

import com.reactive.order.model.entity.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleOrderNotFound(
            OrderNotFoundException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalState(
            IllegalStateException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }
}