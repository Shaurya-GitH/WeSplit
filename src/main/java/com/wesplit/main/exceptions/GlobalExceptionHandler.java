package com.wesplit.main.exceptions;

import com.wesplit.main.payloads.ExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    ResponseEntity<ExceptionMessage> resourceExistsResponse(ResourceAlreadyExistsException e){
        ExceptionMessage message= ExceptionMessage.builder()
                .exception(e.getMessage())
                .exceptionType("ResourceAlreadyExistsException")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ExceptionMessage> resourceNotFoundResponse(ResourceNotFoundException e){
        ExceptionMessage message= ExceptionMessage.builder()
                .exception(e.getMessage())
                .exceptionType("ResourceNotFoundException")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(TransactionFailedException.class)
    ResponseEntity<ExceptionMessage> transactionFailedResponse(TransactionFailedException e){
        ExceptionMessage message= ExceptionMessage.builder()
                .exception(e.getMessage())
                .exceptionType("TransactionFailedException")
                .build();
        return  ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(message);
    }

    @ExceptionHandler(InvalidInputException.class)
    ResponseEntity<ExceptionMessage> invalidInputResponse(InvalidInputException e){
        ExceptionMessage message=ExceptionMessage.builder()
                .exception(e.getMessage())
                .exceptionType("InvalidInputException")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> validationResponse(MethodArgumentNotValidException e){
       Map<String,String> resp=new HashMap<>();
       e.getAllErrors().forEach((error)->{
                String field=((FieldError)error).getField();
                String errMessage=error.getDefaultMessage();
                resp.put(field,errMessage);
               });
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(resp);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    ResponseEntity<ExceptionMessage> tooManyRequestsException(TooManyRequestsException e){
        ExceptionMessage exceptionMessage= ExceptionMessage.builder()
                .exception(e.getMessage())
                .exceptionType("TooManyRequestsException")
                .build();
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exceptionMessage);
    }

}
