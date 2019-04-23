package com.exoscale.circuitbreaker.migration;

public interface Result {

    String getSource();
    Long getValue();

    abstract class AbstractResult implements Result {

        private final Long value;

        AbstractResult(Long value) {
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }

    }

    class Live extends AbstractResult {

        public Live(Long value) {
            super(value);
        }

        @Override
        public String getSource() {
            return "Live";
        }
    }

    class Cached extends AbstractResult {

        public Cached(Long value) {
            super(value);
        }

        @Override
        public String getSource() {
            return "Cache";
        }
    }
}
