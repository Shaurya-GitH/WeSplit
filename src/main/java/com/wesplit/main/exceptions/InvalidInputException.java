package com.wesplit.main.exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String entity,String attribute) {
        super(entity+": "+attribute+" is not allowed");
    }
}
