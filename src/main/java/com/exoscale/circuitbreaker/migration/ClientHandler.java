package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

class ClientHandler {

    private final CircuitBreaker cb;

    ClientHandler(CircuitBreaker cb) {
        this.cb = cb;
    }

    ServerResponse call(ServerRequest req) {
        var timeout = req.param("timeout")
                .map(Integer::parseInt)
                .orElse(0);
        var uri = req.uriBuilder().path("/time").build();
        var command = new ClientCommand(timeout, uri.toString());
        try {
            var result = cb.executeSupplier(command::run);
            return ServerResponse
                    .ok()
                    .body(new Result.Live(result));
        } catch (CircuitBreakerOpenException e) {
            return ServerResponse
                    .ok()
                    .body(new Result.Cached(command.getFallback()));
        }
    }
}
