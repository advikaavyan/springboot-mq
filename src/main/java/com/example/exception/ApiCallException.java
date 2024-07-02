package com.example.exception;

public class ApiCallException extends RuntimeException {

    public ApiCallException() {
        super();
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiCallException(Throwable cause) {
        super(cause);
    }
}