package pl.kurs.biblioteka.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorMessage(
                LocalDateTime.now(),
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                request.getMethod()
        ), NOT_FOUND);
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<ErrorMessage> handleLoanLimitExceededException(LoanLimitExceededException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorMessage(
                LocalDateTime.now(),
                CONFLICT.value(),
                CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                request.getMethod()
        ), CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorMessage error = new ErrorMessage(
                LocalDateTime.now(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI(),
                request.getMethod()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDBIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        String cleanExceptionMessage = "The supplied data conflicts with existing records or violates a data constraint.";
        ErrorMessage error = new ErrorMessage(
                LocalDateTime.now(),
                CONFLICT.value(),
                CONFLICT.getReasonPhrase(),
                cleanExceptionMessage,
                request.getRequestURI(),
                request.getMethod()
        );
        return new ResponseEntity<>(error, CONFLICT);
    }


}


