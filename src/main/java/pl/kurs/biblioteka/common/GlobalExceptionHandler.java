package pl.kurs.biblioteka.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.author.AuthorNotFoundException;
import pl.kurs.biblioteka.book.BookNotFoundException;
import pl.kurs.biblioteka.book.DuplicateIsbnException;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanNotFoundException;
import pl.kurs.biblioteka.loan.NoAvailableCopiesException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAuthorNotFoundException(AuthorNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookNotFoundException(BookNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }
    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLoanNotFoundException(LoanNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }
    @ExceptionHandler(DuplicateIsbnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateIsbnException(DuplicateIsbnException e) {
        return Map.of("blad", e.getMessage());
    }
    @ExceptionHandler(NoAvailableCopiesException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNoAvailableCopiesException(NoAvailableCopiesException e) {
        return Map.of("blad", e.getMessage());
    }
    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleLoanLimitExceededException(LoanLimitExceededException e) {
        return Map.of("blad", e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = String.valueOf(e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage());
        return Map.of("blad", message);
    }
}
