package com.jitsu.common.exception;

public class JitsuException extends RuntimeException {
    public JitsuException(String message) {
        super(message);
    }
    
    public JitsuException(String message, Throwable cause) {
        super(message, cause);
    }
}