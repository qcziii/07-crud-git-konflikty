package pl.kurs.biblioteka.loan;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Loan with id: " + id + " not found");
    }
}
