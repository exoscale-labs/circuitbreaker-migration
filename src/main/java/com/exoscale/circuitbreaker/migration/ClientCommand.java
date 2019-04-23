package com.exoscale.circuitbreaker.migration;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey.Factory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

public class ClientCommand extends HystrixCommand<Result> {

    private static Long cache = null;
    private final int timeout;
    private final String url;
    private final RestTemplate template = new RestTemplate();

    ClientCommand(int timeout, String url) {
        super(Factory.asKey("dummy"));
        this.timeout = timeout;
        this.url = url;
    }

    @Override
    protected Result run() {
        var headers = new HttpHeaders();
        if (timeout > 0) {
            headers.add("timeout", String.valueOf(timeout));
        }
        var entity = new HttpEntity<>(null, headers);
        var result = template
                .exchange(url, GET, entity, Long.class)
                .getBody();
        cache = result;
        return new Result.Live(result);
    }

    @Override
    protected Result getFallback() {
        return new Result.Cached(cache);
    }
}
