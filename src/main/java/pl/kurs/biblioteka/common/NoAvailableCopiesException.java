package pl.kurs.biblioteka.common;

public class NoAvailableCopiesException extends BaseConflictException {

    public NoAvailableCopiesException(String bookTitle) {
        super("Brak wolnych egzemplarzy książki: " + bookTitle);
    }
}
