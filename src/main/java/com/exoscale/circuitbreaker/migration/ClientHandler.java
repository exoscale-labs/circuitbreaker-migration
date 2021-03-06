package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

class ClientHandler {

    private final CircuitBreaker cb;
    private final Cache<Long> cache;
    private final TimeLimiter tl;

    ClientHandler(CircuitBreaker cb, TimeLimiter tl, Cache<Long> cache) {
        this.cb = cb;
        this.cache = cache;
        this.tl = tl;
    }

    ServerResponse call(ServerRequest req) {
        var timeout = req.param("timeout")
                .map(Integer::parseInt)
                .orElse(0);
        var uri = req.uriBuilder().path("/time").build();
        var command = new ClientCommand(timeout, uri.toString());
        var cbDecorated = CircuitBreaker.decorateSupplier(cb, command::run);
        var tlDecorated = TimeLimiter.decorateFutureSupplier(tl, cbDecorated);
        var cacheDecorated = Cache.decorateCallable(cache, tlDecorated);
        return ServerResponse.ok().body(cacheDecorated.get());
    }
}
