package com.exoscale.circuitbreaker.migration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

class ClientCommand {

    private static Long cache = null;
    private final int timeout;
    private final String url;
    private final RestTemplate template = new RestTemplate();

    ClientCommand(int timeout, String url) {
        this.timeout = timeout;
        this.url = url;
    }

    Long run() {
        var headers = new HttpHeaders();
        if (timeout > 0) {
            headers.add("timeout", String.valueOf(timeout));
        }
        var entity = new HttpEntity<>(null, headers);
        var result = template
                .exchange(url, GET, entity, Long.class)
                .getBody();
        cache = result;
        return result;
    }

    Long getFallback() {
        return cache;
    }
}
