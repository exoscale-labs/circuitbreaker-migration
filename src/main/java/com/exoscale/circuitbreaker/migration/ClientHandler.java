package com.exoscale.circuitbreaker.migration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
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
        var cbDecorated = CircuitBreaker.decorateSupplier(cb, command::run);
        return Try.ofSupplier(cbDecorated)
                .map(result -> ServerResponse
                        .ok()
                        .body(new Result.Live(result)))
                .getOrElse(ServerResponse
                        .ok()
                        .body(new Result.Cached(command.getFallback())));
    }
}
