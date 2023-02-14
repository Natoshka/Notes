package com.natoshka.image.exception;

public class AWSException extends RuntimeException {

    public AWSException(String message) {
        super(message);
    }

    public AWSException(String message, Throwable cause) {
        super(message, cause);
    }
}
