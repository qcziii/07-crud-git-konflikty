package pl.kurs.library.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long authorId) {
        super("Author not exist: " + authorId);
    }
}
