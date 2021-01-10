package com.bereda.budgeting_aid.exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message){
        super(message);
    }
}
