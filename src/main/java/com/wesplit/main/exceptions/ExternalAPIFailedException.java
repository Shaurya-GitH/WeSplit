package com.wesplit.main.exceptions;

public class ExternalAPIFailedException extends RuntimeException {
    public ExternalAPIFailedException(String message) {
        super(message);
    }
}
