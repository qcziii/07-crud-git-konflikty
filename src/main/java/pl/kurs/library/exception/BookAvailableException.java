package pl.kurs.library.exception;

public class BookAvailableException extends RuntimeException {
    public BookAvailableException(Long bookId) {
        super("No more available copies of book: " + bookId);
    }
}
