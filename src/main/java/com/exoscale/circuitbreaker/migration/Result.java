package com.exoscale.circuitbreaker.migration;

public interface Result<T> {

    String getSource();
    T getValue();

    abstract class AbstractResult<T> implements Result<T> {

        private final T value;

        AbstractResult(T value) {
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }

    }

    class Live<T> extends AbstractResult<T> {

        public Live(T value) {
            super(value);
        }

        @Override
        public String getSource() {
            return "Live";
        }
    }

    class Cached<T> extends AbstractResult<T> {

        public Cached(T value) {
            super(value);
        }

        @Override
        public String getSource() {
            return "Cache";
        }
    }
}
