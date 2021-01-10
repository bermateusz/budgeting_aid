package com.bereda.budgeting_aid.exceptions;

public class BudgetCategoryNotFoundException extends RuntimeException {

    public BudgetCategoryNotFoundException(final String message){
        super(message);
    }
}
