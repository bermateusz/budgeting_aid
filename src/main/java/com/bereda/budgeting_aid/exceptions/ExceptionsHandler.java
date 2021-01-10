package com.bereda.budgeting_aid.exceptions;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(TransferException.class)
    ResponseEntity<ApiError> handleTransferException(final TransferException ex) {
        log.error("Exception occurred", ex);
        return new ResponseEntity<>(ApiError.of(ex.getLocalizedMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ApiError> handleValidationException(final ValidationException ex){
        log.error("Exception occurred", ex);
        return new ResponseEntity<>(ApiError.of(ex.getLocalizedMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(BudgetCategoryNotFoundException.class)
    ResponseEntity<ApiError>handleBudgetCategoryNotFound(final BudgetCategoryNotFoundException ex){
        log.error("Exception occurred", ex);
        return new ResponseEntity<>(ApiError.of(ex.getLocalizedMessage()), NOT_FOUND);
    }

    @Value(staticConstructor = "of")
    public static class ApiError {
        String message;
    }
}
