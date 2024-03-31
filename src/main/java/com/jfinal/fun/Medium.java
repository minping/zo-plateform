package com.jfinal.fun;

@FunctionalInterface
public interface Medium<T, R> {
    boolean test(T var1);

    default Medium<T, R> negate() {
        return (t) -> {
            return !this.test(t);
        };
    }

    default R map(T t) {
        return (R) t;
    }

    default void apply(R r) {
    }

}
