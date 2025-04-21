package com.wesplit.main.exceptions;

public class TooManyRequestsException extends RuntimeException{
    public TooManyRequestsException(Integer number,String apiName,String time){
        super("only "+number+" "+apiName+" allowed in "+time);
    }
}
