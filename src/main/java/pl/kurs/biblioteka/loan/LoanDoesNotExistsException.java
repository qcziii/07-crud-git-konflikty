package pl.kurs.biblioteka.loan;

public class LoanDoesNotExistsException extends RuntimeException {
    public LoanDoesNotExistsException(String message) {
        super(message);
    }
}
