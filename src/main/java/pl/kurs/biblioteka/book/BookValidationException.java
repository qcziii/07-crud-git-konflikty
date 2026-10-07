package pl.kurs.biblioteka.book;

public class BookValidationException extends RuntimeException {
    public BookValidationException(String message) {
        super(message);
    }
}
