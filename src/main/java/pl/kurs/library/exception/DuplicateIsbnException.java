package pl.kurs.library.exception;

public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String isbn) {
        super("Book with ISBN already exist" + isbn);
    }

}
