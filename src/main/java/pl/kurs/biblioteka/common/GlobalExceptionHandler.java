package pl.kurs.biblioteka.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.author.AuthorNotFoundException;
import pl.kurs.biblioteka.book.BookNotFoundException;
import pl.kurs.biblioteka.book.DuplicateIsbnException;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanNotFoundException;
import pl.kurs.biblioteka.loan.NoBooksAvailableToBorrow;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(Exception e) {
        return Map.of("blad", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorMessage> DuplicateIsbnException(DuplicateIsbnException e, HttpServletRequest request) {
        HttpStatus status = CONFLICT;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorMessage> authorNotFoundException(AuthorNotFoundException e, HttpServletRequest request) {
        HttpStatus status = NOT_FOUND;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorMessage> bookNotFoundException(BookNotFoundException e, HttpServletRequest request) {
        HttpStatus status = NOT_FOUND;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorMessage> loanNotFoundException(LoanNotFoundException e, HttpServletRequest request) {
        HttpStatus status = NOT_FOUND;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<ErrorMessage> loanLimitExceededException(LoanLimitExceededException e, HttpServletRequest request) {
        HttpStatus status = CONFLICT;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(NoBooksAvailableToBorrow.class)
    public ResponseEntity<ErrorMessage> noBooksAvailableToBorrow(NoBooksAvailableToBorrow e, HttpServletRequest request) {
        HttpStatus status = CONFLICT;
        ErrorMessage errorMessage = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return ResponseEntity
                .status(status)
                .body(errorMessage);
    }
}
