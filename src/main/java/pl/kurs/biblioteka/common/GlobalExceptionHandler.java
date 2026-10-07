package pl.kurs.biblioteka.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.author.AuthorDoesNotExistsException;
import pl.kurs.biblioteka.author.AuthorValidationException;
import pl.kurs.biblioteka.book.BookBadRequestException;
import pl.kurs.biblioteka.book.BookDoesNotExistsException;
import pl.kurs.biblioteka.book.BookValidationException;
import pl.kurs.biblioteka.loan.LoanDoesNotExistsException;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(Exception e) {
        return Map.of("Error", "Unexpected server error");
    }

    @ExceptionHandler(BookDoesNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> bookDoesNotExistsException(BookDoesNotExistsException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(AuthorDoesNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> authorDoesNotExistsException(AuthorDoesNotExistsException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(LoanDoesNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> loanDoesNotExistsException(LoanDoesNotExistsException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> bookValidationException(BookValidationException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(AuthorValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> authorValidationException(AuthorValidationException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bookBadRequestException(BookBadRequestException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> loanLimitExceededException(LoanLimitExceededException e) {
        return Map.of("Error", String.valueOf(e.getMessage()));
    }
}
