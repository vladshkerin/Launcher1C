package com.vladshkerin.exception;

public class NotFoundPropertyException extends Exception {

    public NotFoundPropertyException(String message) {
        super(message);
    }

    public NotFoundPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundPropertyException(Throwable cause) {
        super(cause);
    }
}
