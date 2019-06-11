package com.exoscale.circuitbreaker.migration;

import io.vavr.control.Try;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Cache<T> {

    private T value;

    static <T> Supplier<Result<T>> decorateCallable(Cache<T> cache, Callable<T> callable) {
        return Try.ofCallable(callable)
                .fold(
                        throwable -> () -> new Result.Cached<>(cache.value),
                        value -> {
                            cache.value = value;
                            return () -> new Result.Live<>(value);
                        });
    }
}