package pl.kurs.biblioteka.common;

public class EmptyTitleException extends RuntimeException {
    public EmptyTitleException(String message) {
        super(message);
    }
}
