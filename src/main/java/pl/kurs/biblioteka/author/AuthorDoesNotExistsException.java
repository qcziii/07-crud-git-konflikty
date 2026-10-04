package pl.kurs.biblioteka.author;

public class AuthorDoesNotExistsException extends RuntimeException {
    public AuthorDoesNotExistsException(String message) {
        super(message);
    }
}
