package ru.tinkoff.tma.caen.el;

public class Result {

    private final Object value;

    private Result(Object value) {
        this.value = value;
    }

    public static Result of(Object value) {
        return new Result(value);
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }
}
