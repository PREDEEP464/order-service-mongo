package com.reactive.order.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
public class MongoConfig {

    private static final String MONGO_URI =
            "mongodb://localhost:27017/orders_db";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MONGO_URI);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(
            MongoClient mongoClient) {

        return new ReactiveMongoTemplate(
                mongoClient,
                "orders_db"
        );
    }
}