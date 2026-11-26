package pl.kurs.library.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    @ExceptionHandler({DuplicateIsbnException.class,
            NoAvailableCopiesException.class,
            LoanLimitExceededException.class})

    public ResponseEntity<ErrorMessage> conflict(RuntimeException e, HttpServletRequest request) {
        return error(CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler({AuthorDoesNotExistException.class,
            LoanNotFoundException.class,
            BookNotFoundException.class})
    public ResponseEntity<ErrorMessage> notFound(RuntimeException e, HttpServletRequest request) {
        return error(NOT_FOUND, e.getMessage(), request);
    }

    private ResponseEntity<ErrorMessage> error(HttpStatus status, String message, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(status.value())
                .status(status.getReasonPhrase())
                .message(message)
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), status);
    }
}
