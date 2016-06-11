package org.miasi.exception;

public class TrelloException extends Exception {

    public TrelloException(String message) {
        super(message);
    }

    public TrelloException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrelloException(Throwable cause) {
        super(cause);
    }
}
