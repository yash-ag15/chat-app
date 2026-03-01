package com.yash.chat_app.exception;

public class JwtInvalidException extends RuntimeException {
    public JwtInvalidException(String message){
        super(message);
    }
}
