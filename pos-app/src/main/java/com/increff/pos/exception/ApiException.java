package com.increff.pos.exception;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiException(String string) {
        super(string);
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
