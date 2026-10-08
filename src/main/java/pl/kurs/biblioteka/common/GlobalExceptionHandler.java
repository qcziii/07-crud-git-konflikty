package pl.kurs.biblioteka.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLoanNotFound(LoanNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookNotFound(BookNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAuthorNotFound(AuthorNotFoundException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(Exception e) {
        return Map.of("blad", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return Map.of("blad", message);
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateIsbn(DuplicateIsbnException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleLoanLimitExceeded(LoanLimitExceededException e) {
        return Map.of("blad", e.getMessage());
    }

    @ExceptionHandler(BookUnavailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleBookUnavailable(BookUnavailableException e) {
        return Map.of("blad", e.getMessage());
    }
}
