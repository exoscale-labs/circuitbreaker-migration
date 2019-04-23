package com.exoscale.circuitbreaker.migration;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.concurrent.TimeUnit;

class TimeHandler {

    ServerResponse time(ServerRequest req) {
        var timeouts = req.headers().header("timeout");
        if (!timeouts.isEmpty()) {
            try {
                var timeout = Integer.parseInt(timeouts.iterator().next());
                TimeUnit.SECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                // Don't bother me
            }
            return ServerResponse.status(500).build();
        } else {
            return ServerResponse
                    .ok()
                    .body(System.currentTimeMillis());
        }
    }
}
