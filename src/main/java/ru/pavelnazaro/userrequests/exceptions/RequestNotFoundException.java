package ru.pavelnazaro.userrequests.exceptions;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(String message) {
        super(message);
    }

}