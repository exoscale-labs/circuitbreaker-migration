package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routes(CircuitBreaker cb) {
        return route()
                .GET("/time", new TimeHandler()::time)
                .GET("/", new ClientHandler(cb)::call)
                .build();
    }

    @Bean
    CircuitBreaker circuitBreaker() {
        var cfg = CircuitBreakerConfig.custom()
                .ringBufferSizeInClosedState(5)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .build();
        return CircuitBreaker.of("circuit-breaker", cfg);
    }
}
