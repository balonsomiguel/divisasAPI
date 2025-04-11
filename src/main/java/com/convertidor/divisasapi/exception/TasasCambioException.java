package com.convertidor.divisasapi.exception;

public class TasasCambioException extends RuntimeException {

    public TasasCambioException(String message) {
        super(message);
    }

    public TasasCambioException(String message, Throwable cause) {
        super(message, cause);
    }
}
