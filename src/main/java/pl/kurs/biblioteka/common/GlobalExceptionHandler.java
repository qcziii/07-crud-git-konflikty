package pl.kurs.biblioteka.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmptyTitleException.class)
    public ResponseEntity<ErrorMessage> handleEmptyTitle(EmptyTitleException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .code(BAD_REQUEST.value())
                .status(BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleBookNotFound(BookNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .code(NOT_FOUND.value())
                .status(NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), NOT_FOUND);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleAuthorNotFound(AuthorNotFoundException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .code(NOT_FOUND.value())
                .status(NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .code(CONFLICT.value())
                .status(CONFLICT.getReasonPhrase())
                .message("Book with this ISBN already exists")
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), CONFLICT);
    }
}
