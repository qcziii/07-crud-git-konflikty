package pl.kurs.biblioteka.common;

public class ISBNDuplicatedException extends RuntimeException {
    public ISBNDuplicatedException(String message) {
        super(message);
    }
}
