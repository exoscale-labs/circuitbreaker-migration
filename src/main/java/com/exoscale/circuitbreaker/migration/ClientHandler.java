package com.exoscale.circuitbreaker.migration;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

class ClientHandler {

    ServerResponse call(ServerRequest req) {
        var timeout = req.param("timeout")
                .map(Integer::parseInt)
                .orElse(0);
        var uri = req.uriBuilder().path("/time").build();
        var command = new ClientCommand(timeout, uri.toString());
        var result = command.execute();
        return ServerResponse
                .ok()
                .body(result);
    }
}
