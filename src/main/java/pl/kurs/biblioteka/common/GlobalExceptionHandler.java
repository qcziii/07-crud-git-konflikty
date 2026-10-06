package pl.kurs.biblioteka.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.exception.AuthorNotFoundException;
import pl.kurs.biblioteka.exception.BookNotFoundException;
import pl.kurs.biblioteka.exception.LoanNotFoundException;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handle(Exception e) {
//        return Map.of("blad", String.valueOf(e.getMessage()));
//    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookNotFoundException(BookNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAuthorNotFoundException(AuthorNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleLoanNotFoundException(LoanNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return "Data integrity violation conflict";
    }
    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String LoanLimitExceededException(LoanLimitExceededException e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String IllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }
}
