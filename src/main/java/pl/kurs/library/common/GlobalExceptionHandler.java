package pl.kurs.library.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.library.author.AuthorDoesNotExistException;
import pl.kurs.library.book.BookNotFoundException;
import pl.kurs.library.book.DuplicateIsbnException;
import pl.kurs.library.loan.LoanLimitExceededException;
import pl.kurs.library.loan.LoanNotFoundException;
import pl.kurs.library.loan.NoAvailableCopiesException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(Exception e) {
        return Map.of("error", "wystąpił nieoczekiwany błąd");
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorMessage> duplicateIsbnException(DuplicateIsbnException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(CONFLICT.value())
                .status(CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), CONFLICT);
    }


    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorMessage> bookNotFoundException(BookNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(NOT_FOUND.value())
                .status(NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), NOT_FOUND);
    }

    @ExceptionHandler(NoAvailableCopiesException.class)
    public ResponseEntity<ErrorMessage> noAvailableCopiesException(NoAvailableCopiesException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(CONFLICT.value())
                .status(CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), CONFLICT);
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<ErrorMessage> loanLimitExceededException(LoanLimitExceededException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(CONFLICT.value())
                .status(CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), CONFLICT);
    }

    @ExceptionHandler(AuthorDoesNotExistException.class)
    public ResponseEntity<ErrorMessage> authorDoesNotExistException(AuthorDoesNotExistException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(NOT_FOUND.value())
                .status(NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), NOT_FOUND);
    } @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorMessage> loanNotFoundException(LoanNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(NOT_FOUND.value())
                .status(NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), NOT_FOUND);
    }
}
