package pl.kurs.library.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookID) {
        super("Book "+bookID+" not exist");
    }
}
