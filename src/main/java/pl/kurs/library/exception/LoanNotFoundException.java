package pl.kurs.library.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Loan " + id + " not exist ");
    }
}
