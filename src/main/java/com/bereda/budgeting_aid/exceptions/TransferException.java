package com.bereda.budgeting_aid.exceptions;

public class TransferException extends RuntimeException{

    public TransferException(final String message){
        super(message);
    }
}
