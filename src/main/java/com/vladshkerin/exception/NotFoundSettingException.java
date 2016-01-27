package com.vladshkerin.exception;

public class NotFoundSettingException extends Exception {

    public NotFoundSettingException(String message) {
        super(message);
    }

    public NotFoundSettingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundSettingException(Throwable cause) {
        super(cause);
    }
}
