package com.example.exception;

import com.example.dto.IncomingMessageData;
import lombok.Data;

@Data
public class BackoutException extends RuntimeException {
    IncomingMessageData incomingMessageData;

    public BackoutException(IncomingMessageData incomingMessageData) {
        super();
        this.incomingMessageData = incomingMessageData;
    }

    public BackoutException(IncomingMessageData incomingMessageData, String message) {
        super(message);
        this.incomingMessageData = incomingMessageData;
    }

    public BackoutException(IncomingMessageData incomingMessageData, String message, Throwable cause) {
        super(message, cause);
        this.incomingMessageData = incomingMessageData;
    }

    public BackoutException(IncomingMessageData incomingMessageData, Throwable cause) {
        super(cause);
        this.incomingMessageData = incomingMessageData;
    }
}