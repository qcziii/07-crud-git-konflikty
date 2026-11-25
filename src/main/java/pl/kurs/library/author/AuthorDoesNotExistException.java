package pl.kurs.library.author;

public class AuthorDoesNotExistException extends RuntimeException {
    public AuthorDoesNotExistException(String message) {
        super(message);
    }
}
