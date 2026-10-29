package pl.kurs.biblioteka.common;

public abstract class BaseConflictException extends RuntimeException {
    protected BaseConflictException(String message) {
        super(message);
    }
}
