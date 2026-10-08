package pl.kurs.biblioteka.loan;

public class NoAvailableCopiesException extends RuntimeException {

    public NoAvailableCopiesException(String title) {
        super("Brak wolnych egzemplarzy książki " + title);
    }
}
