package pl.kurs.biblioteka.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NoFreeBookException extends RuntimeException {
    public NoFreeBookException(String message) {
        super(message);
    }
}
