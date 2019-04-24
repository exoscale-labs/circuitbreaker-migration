package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

class ClientHandler {

    private final CircuitBreaker cb;
    private final Cache<Long> cache;

    ClientHandler(CircuitBreaker cb, Cache<Long> cache) {
        this.cb = cb;
        this.cache = cache;
    }

    ServerResponse call(ServerRequest req) {
        var timeout = req.param("timeout")
                .map(Integer::parseInt)
                .orElse(0);
        var uri = req.uriBuilder().path("/time").build();
        var command = new ClientCommand(timeout, uri.toString());
        var cbDecorated = CircuitBreaker.decorateSupplier(cb, command::run);
        var cacheDecorated = Cache.decorateSupplier(cache, cbDecorated);
        return Try.ofSupplier(cacheDecorated)
                .map(result -> ServerResponse.ok().body(result))
                .get();
    }
}
