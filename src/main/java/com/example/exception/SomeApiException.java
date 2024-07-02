package com.example.exception;

public class SomeApiException extends Exception {

    public SomeApiException() {
        super();
    }

    public SomeApiException(String message) {
        super(message);
    }

    public SomeApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public SomeApiException(Throwable cause) {
        super(cause);
    }
}
    
