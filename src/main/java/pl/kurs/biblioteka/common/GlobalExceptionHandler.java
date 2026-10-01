package pl.kurs.biblioteka.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.book.BookDoesNotExistException;
import pl.kurs.biblioteka.book.BookValidationException;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(Exception e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> bookDoesNotExistsException(BookDoesNotExistException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> bookValidationException(BookValidationException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> loanLimitExceededException(LoanLimitExceededException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }
}
