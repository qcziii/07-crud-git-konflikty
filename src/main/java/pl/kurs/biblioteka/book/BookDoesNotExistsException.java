package pl.kurs.biblioteka.book;

public class BookDoesNotExistsException extends RuntimeException {
    public BookDoesNotExistsException(String message) {
        super(message);
    }
}
