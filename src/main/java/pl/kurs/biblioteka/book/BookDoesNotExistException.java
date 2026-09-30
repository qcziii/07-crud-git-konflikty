package pl.kurs.biblioteka.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookDoesNotExistException extends RuntimeException {
    public BookDoesNotExistException(String message) {
        super(message);
    }
}
