package com.ToyRentalService.exception.handler;

public class AuthException extends RuntimeException{
    public AuthException(String message){
        super(message);
    }
}
