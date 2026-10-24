package pl.kurs.biblioteka.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.kurs.biblioteka.author.AuthorNotFoundException;
import pl.kurs.biblioteka.book.BookWithIdNotFoundException;
import pl.kurs.biblioteka.book.BookWithIsbnNotFoundException;
import pl.kurs.biblioteka.book.DuplicateOfIsbnExeption;
import pl.kurs.biblioteka.loan.LoanLimitExceededException;
import pl.kurs.biblioteka.loan.LoanWithIdNotFoundException;
import pl.kurs.biblioteka.loan.NoBooksAvaliableException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({org.springframework.web.bind.MethodArgumentNotValidException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.bind.ServletRequestBindingException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(Exception e) {
        return Map.of("Error: ", "Unexpected server error");
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAuthorNotFound(Exception e) {
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookWithIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookIdNotFound(Exception e) {
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(BookWithIsbnNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookIsbnNotFound(Exception e) {
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(DuplicateOfIsbnExeption.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleIsbnDuplicate(Exception e){
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }

    @ExceptionHandler(LoanWithIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLoanIdNotFound(Exception e){
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        if (message != null && (message.toLowerCase().contains("isbn"))) {
            return Map.of("Error: ", "Book with the same ISBN exists");
        }
        return Map.of("Error: ", "Data integrity error");
    }

    @ExceptionHandler(NoBooksAvaliableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNoBooksAvaliableException(Exception e){
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }
}
