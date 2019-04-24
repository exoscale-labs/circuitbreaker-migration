package com.exoscale.circuitbreaker.migration;

import io.vavr.control.Try;

import java.util.function.Supplier;

public class Cache<T> {

    private T value;

    static <T> Supplier<Result<T>> decorateSupplier(Cache<T> cache, Supplier<T> supplier) {
        return Try.ofSupplier(supplier)
                .fold(
                        throwable -> () -> new Result.Cached<>(cache.value),
                        value -> {
                            cache.value = value;
                            return () -> new Result.Live<>(value);
                        });
    }
}