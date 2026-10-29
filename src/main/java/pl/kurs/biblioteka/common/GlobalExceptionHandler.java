package pl.kurs.biblioteka.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(HttpServletRequest request) {
        return buildResponseEntityErrorMessage(CONFLICT, "The operation conflicts with existing data.", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponseEntityErrorMessage(NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BaseConflictException.class)
    public ResponseEntity<ErrorMessage> handleBaseConflict(BaseConflictException ex, HttpServletRequest request) {
        return buildResponseEntityErrorMessage(CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponseEntityErrorMessage(BAD_REQUEST, message, request);
    }

    private ResponseEntity<ErrorMessage> buildResponseEntityErrorMessage(HttpStatus httpStatus, String message, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .dateTime(LocalDateTime.now())
                .code(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .message(message)
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build(), httpStatus);
    }
}
