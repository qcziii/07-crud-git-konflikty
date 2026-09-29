package pl.kurs.biblioteka.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.author.AuthorNotFoundException;
import pl.kurs.biblioteka.book.BookNotFoundException;
import pl.kurs.biblioteka.book.DuplicateIsbnException;
import pl.kurs.biblioteka.book.NoFreeBookException;
import pl.kurs.biblioteka.common.dto.ErrorResponse;
import pl.kurs.biblioteka.common.dto.FieldError;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanNotFoundException;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(BookNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Set<FieldError>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(
                exception.getBindingResult().getFieldErrors().stream()
                        .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                        .collect(Collectors.toCollection(TreeSet::new)));
    }

    @ExceptionHandler(NoFreeBookException.class)
    public ResponseEntity<ErrorResponse> handleNoFreeBookException(NoFreeBookException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleLoanLimitExceededException(LoanLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanNotFoundException(LoanNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanAuthorNotFoundException(AuthorNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateIsbnException(DuplicateIsbnException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), "Naruszenie integralności danych"));
    }
}
