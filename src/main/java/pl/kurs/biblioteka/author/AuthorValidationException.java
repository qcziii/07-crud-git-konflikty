package pl.kurs.biblioteka.author;

public class AuthorValidationException extends RuntimeException {
    public AuthorValidationException(String message) {
        super(message);
    }
}
