package com.unknown.post.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class BaseConfiguration {
    private static final MongoDBContainer container;

    static {
        container = new MongoDBContainer("mongo:latest").withReuse(true)
                .withCommand("--replSet", "rs0", "--bind_ip_all");
        container.start();
    }

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        return container;
    }
}
