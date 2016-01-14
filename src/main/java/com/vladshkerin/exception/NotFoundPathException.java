package com.vladshkerin.exception;

public class NotFoundPathException extends Exception {

    public NotFoundPathException(String message) {
        super(message);
    }

    public NotFoundPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundPathException(Throwable cause) {
        super(cause);
    }
}
