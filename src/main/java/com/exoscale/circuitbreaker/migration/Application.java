package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
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
    RouterFunction<ServerResponse> routes(CircuitBreaker cb, TimeLimiter tl, Cache<Long> cache) {
        return route()
                .GET("/time", new TimeHandler()::time)
                .GET("/", new ClientHandler(cb, tl, cache)::call)
                .build();
    }

    @Bean
    TimeLimiter timeLimiter() {
        return TimeLimiter.of(Duration.ofSeconds(1));
    }

    @Bean
    CircuitBreaker circuitBreaker() {
        var cfg = CircuitBreakerConfig.custom()
                .ringBufferSizeInClosedState(5)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .build();
        return CircuitBreaker.of("circuit-breaker", cfg);
    }

    @Bean
    Cache<Long> cache() {
        return new Cache<>();
    }
}
