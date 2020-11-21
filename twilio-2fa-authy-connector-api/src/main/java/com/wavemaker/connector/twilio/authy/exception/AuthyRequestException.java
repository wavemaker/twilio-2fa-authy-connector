package com.wavemaker.connector.twilio.authy.exception;

public class AuthyRequestException extends RuntimeException{

    public AuthyRequestException(String message) {
        super(message);
    }

    public AuthyRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
