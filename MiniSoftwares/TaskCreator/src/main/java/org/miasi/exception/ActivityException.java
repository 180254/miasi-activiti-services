package org.miasi.exception;

public class ActivityException extends Exception {

    public ActivityException(String message) {
        super(message);
    }

    public ActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityException(Throwable cause) {
        super(cause);
    }
}
