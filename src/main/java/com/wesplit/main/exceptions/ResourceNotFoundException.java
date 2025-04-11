package com.wesplit.main.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity,String attribute) {
        super(attribute+" of "+entity+" is not found ");
    }
}
