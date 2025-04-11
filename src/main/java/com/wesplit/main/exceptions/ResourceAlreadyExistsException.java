package com.wesplit.main.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
  public ResourceAlreadyExistsException(String entity, String attribute) {
    super(entity+" " + attribute+" already exists");
  }
}
