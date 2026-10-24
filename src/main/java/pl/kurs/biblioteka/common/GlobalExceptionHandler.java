package pl.kurs.biblioteka.common;

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

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
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

    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleLoanLimitExceededException(Exception e){
        return Map.of("Error: ", String.valueOf(e.getMessage()));
    }
}
